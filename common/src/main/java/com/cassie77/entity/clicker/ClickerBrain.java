package com.cassie77.entity.clicker;

import com.cassie77.ModSensors;
import com.cassie77.entity.bloater.BloaterBrain;
import com.cassie77.entity.bloater.BloaterEntity;
import com.cassie77.entity.bloater.ThrowTask;
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
import net.minecraft.world.entity.ai.behavior.warden.Sniffing;
import net.minecraft.world.entity.ai.behavior.warden.TryToSniff;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;

public class ClickerBrain {

    private static final float STROLL_SPEED = 0.5F;
    private static final float CELEBRATE_TIME = 0.7F;
    private static final float RANGED_APPROACH_SPEED = 1.2F;
    private static final int MELEE_ATTACK_INTERVAL = 10;
    public static final int ROAR_DURATION = Mth.ceil(20.0F);
    private static final int SNIFF_DURATION = Mth.ceil(83.2F);

    private static final List<SensorType<? extends Sensor<? super ClickerEntity>>> SENSORS;
    private static final List<MemoryModuleType<?>> MEMORY_MODULES;

    public ClickerBrain() {
    }

    public static void updateActivities(ClickerEntity clicker) {
        clicker.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.ROAR, Activity.FIGHT, Activity.INVESTIGATE, Activity.SNIFF, Activity.IDLE));
    }

    protected static Brain<?> create(ClickerEntity clicker, Dynamic<?> dynamic) {
        Brain.Provider<ClickerEntity> provider = Brain.provider(MEMORY_MODULES, SENSORS);
        Brain<ClickerEntity> brain = provider.makeBrain(dynamic);
        addCoreActivities(brain);
        addIdleActivities(brain);
        addRoarActivities(brain);
        addFightActivities(clicker, brain);
        addInvestigateActivities(brain);
        addSniffActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    static ImmutableList<BehaviorControl<? super ClickerEntity>> idleTasks = ImmutableList.of(
            ClickerFindRoarTargetTask.create(ClickerEntity::getPrimeSuspect),
            TryToSniff.create(),
            new RunOne<>(
                    ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT),
                    ImmutableList.of(
                            Pair.of(RandomStroll.stroll(STROLL_SPEED), 2),
                            Pair.of(new DoNothing(60, 200), 1)
                    )
            )
    );

    private static void addCoreActivities(Brain<ClickerEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Swim<>(0.8F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void addIdleActivities(Brain<ClickerEntity> brain) {
        brain.addActivity(Activity.IDLE, 10, idleTasks);
    }

    private static void addInvestigateActivities(Brain<ClickerEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.INVESTIGATE, 5, ImmutableList.of(
                ClickerFindRoarTargetTask.create(ClickerEntity::getPrimeSuspect),
                GoToTargetLocation.create(MemoryModuleType.DISTURBANCE_LOCATION, 2, CELEBRATE_TIME)
        ), MemoryModuleType.DISTURBANCE_LOCATION);
    }

    private static void addSniffActivities(Brain<ClickerEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.SNIFF, 5, ImmutableList.of(
                ClickerFindRoarTargetTask.create(ClickerEntity::getPrimeSuspect),
                new ClickerSniffTask<>(SNIFF_DURATION)
        ), MemoryModuleType.IS_SNIFFING);
    }

    private static void addRoarActivities(Brain<ClickerEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(
                Activity.ROAR,
                10,
                ImmutableList.of(new ClickerRoarTask()),
                MemoryModuleType.ROAR_TARGET
        );
    }

    private static void addFightActivities(ClickerEntity clicker, Brain<ClickerEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(StopAttackingIfTargetInvalid.create((serverLevel, livingEntity) -> !clicker.getAngriness().isAngry() || !clicker.isValidTarget(livingEntity), ClickerBrain::removeDeadSuspect, false), SetEntityLookTarget.create((livingEntity) -> isTargeting(clicker, livingEntity), (float)clicker.getAttributeValue(Attributes.FOLLOW_RANGE)), SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(RANGED_APPROACH_SPEED), MeleeAttack.create(MELEE_ATTACK_INTERVAL)), MemoryModuleType.ATTACK_TARGET);
    }


    private static boolean isTargeting(ClickerEntity clicker, LivingEntity entity) {
        return clicker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter((livingEntity) -> livingEntity == entity).isPresent();
    }

    private static void removeDeadSuspect(ServerLevel world, ClickerEntity clicker, LivingEntity target) {
        if (!clicker.isValidTarget(target)) {
            clicker.removeSuspect(target);
        }
    }

    public static void lookAtDisturbance(ClickerEntity clicker, BlockPos pos) {
        if (clicker.level().getWorldBorder().isWithinBounds(pos) && clicker.getPrimeSuspect().isEmpty() && clicker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()) {
            clicker.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 100L);
            clicker.getBrain().setMemoryWithExpiry(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos), 100L);
            clicker.getBrain().setMemoryWithExpiry(MemoryModuleType.DISTURBANCE_LOCATION, pos, 100L);
            clicker.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
    }

    static {
        SENSORS = List.of(SensorType.NEAREST_PLAYERS, ModSensors.CLICKER_ENTITY_SENSOR);
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
