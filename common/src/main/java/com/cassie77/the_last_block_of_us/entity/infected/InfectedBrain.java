package com.cassie77.the_last_block_of_us.entity.infected;

import com.cassie77.the_last_block_of_us.ModSensors;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;

public class InfectedBrain {

    private static final float STROLL_SPEED = 0.5F;
    private static final float CELEBRATE_TIME = 0.7F;
    private static final float RANGED_APPROACH_SPEED = 1.2F;
    private static final int MELEE_ATTACK_INTERVAL = 10;
    public static final int ROAR_DURATION = Mth.ceil(20.0F);
    private static final int SNIFF_DURATION = Mth.ceil(83.2F);

    private static final List<SensorType<? extends Sensor<? super InfectedEntity>>> SENSORS;
    private static final List<MemoryModuleType<?>> MEMORY_MODULES;

    public InfectedBrain() {
    }

    public static void updateActivities(InfectedEntity infected) {
        infected.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.ROAR, Activity.FIGHT, Activity.INVESTIGATE, Activity.SNIFF, Activity.IDLE));
    }

    protected static Brain<?> create(InfectedEntity infected, Dynamic<?> dynamic) {
        Brain.Provider<InfectedEntity> provider = Brain.provider(MEMORY_MODULES, SENSORS);
        Brain<InfectedEntity> brain = provider.makeBrain(dynamic);
        addCoreActivities(brain);
        addIdleActivities(brain);
        addRoarActivities(brain);
        addFightActivities(infected, brain);
        addInvestigateActivities(brain);
        addSniffActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    static ImmutableList<BehaviorControl<? super InfectedEntity>> idleTasks = ImmutableList.of(
            InfectedFindRoarTargetTask.create(InfectedEntity::getPrimeSuspect),
            InfectedTryToSniff.create(),
            new RunOne<>(
                    ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT),
                    ImmutableList.of(
                            Pair.of(RandomStroll.stroll(STROLL_SPEED), 2),
                            Pair.of(new DoNothing(60, 200), 1)
                    )
            )
    );

    private static void addCoreActivities(Brain<InfectedEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Swim<>(0.8F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void addIdleActivities(Brain<InfectedEntity> brain) {
        brain.addActivity(Activity.IDLE, 10, idleTasks);
    }

    private static void addInvestigateActivities(Brain<InfectedEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.INVESTIGATE, 5, ImmutableList.of(
                InfectedFindRoarTargetTask.create(InfectedEntity::getPrimeSuspect),
                GoToTargetLocation.create(MemoryModuleType.DISTURBANCE_LOCATION, 2, CELEBRATE_TIME)
        ), MemoryModuleType.DISTURBANCE_LOCATION);
    }

    private static void addSniffActivities(Brain<InfectedEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.SNIFF, 5, ImmutableList.of(
                InfectedFindRoarTargetTask.create(InfectedEntity::getPrimeSuspect),
                new InfectedSniffTask<>(SNIFF_DURATION)
        ), MemoryModuleType.IS_SNIFFING);
    }

    private static void addRoarActivities(Brain<InfectedEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(
                Activity.ROAR,
                10,
                ImmutableList.of(new InfectedRoarTask()),
                MemoryModuleType.ROAR_TARGET
        );
    }

    protected static void addFightActivities(InfectedEntity infected, Brain<InfectedEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(StopAttackingIfTargetInvalid.create((serverLevel, livingEntity) -> !infected.getAngriness().isAngry() || !infected.isValidTarget(livingEntity), InfectedBrain::removeDeadSuspect, false), SetEntityLookTarget.create((livingEntity) -> isTargeting(infected, livingEntity), (float)infected.getAttributeValue(Attributes.FOLLOW_RANGE)), SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(RANGED_APPROACH_SPEED), MeleeAttack.create(MELEE_ATTACK_INTERVAL)), MemoryModuleType.ATTACK_TARGET);
    }


    protected static boolean isTargeting(InfectedEntity infected, LivingEntity entity) {
        return infected.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter((livingEntity) -> livingEntity == entity).isPresent();
    }

    protected static void removeDeadSuspect(ServerLevel world, InfectedEntity infected, LivingEntity target) {
        if (!infected.isValidTarget(target)) {
            infected.removeSuspect(target);
        }
    }

    public static void lookAtDisturbance(InfectedEntity infected, BlockPos pos) {
        if (infected.level().getWorldBorder().isWithinBounds(pos) && infected.getPrimeSuspect().isEmpty() && infected.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()) {
            infected.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 100L);
            infected.getBrain().setMemoryWithExpiry(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos), 100L);
            infected.getBrain().setMemoryWithExpiry(MemoryModuleType.DISTURBANCE_LOCATION, pos, 100L);
            infected.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
    }

    static {
        SENSORS = List.of(SensorType.NEAREST_PLAYERS, ModSensors.INFECTED_ENTITY_SENSOR);
        MEMORY_MODULES = List.of(
                MemoryModuleType.NEAREST_LIVING_ENTITIES,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                MemoryModuleType.NEAREST_VISIBLE_PLAYER,
                MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
                MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
                MemoryModuleType.LOOK_TARGET,
                MemoryModuleType.WALK_TARGET,
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                MemoryModuleType.PATH,
                MemoryModuleType.ATTACK_TARGET,
                MemoryModuleType.ATTACK_COOLING_DOWN,
                MemoryModuleType.NEAREST_ATTACKABLE,
                MemoryModuleType.ROAR_TARGET,
                MemoryModuleType.DISTURBANCE_LOCATION,
                MemoryModuleType.RECENT_PROJECTILE,
                MemoryModuleType.IS_SNIFFING,
                MemoryModuleType.IS_EMERGING,
                MemoryModuleType.ROAR_SOUND_DELAY,
                MemoryModuleType.DIG_COOLDOWN,
                MemoryModuleType.ROAR_SOUND_COOLDOWN,
                MemoryModuleType.SNIFF_COOLDOWN,
                MemoryModuleType.TOUCH_COOLDOWN,
                MemoryModuleType.VIBRATION_COOLDOWN
        );
    }
}
