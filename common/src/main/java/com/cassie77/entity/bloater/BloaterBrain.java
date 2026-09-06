package com.cassie77.entity.bloater;

import com.cassie77.ModSensors;
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
import net.minecraft.world.entity.ai.behavior.warden.SetWardenLookTarget;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.ai.behavior.warden.TryToSniff;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;

public class BloaterBrain {

    private static final float STROLL_SPEED = 0.5F;
    private static final float CELEBRATE_TIME = 0.7F;
    private static final float RANGED_APPROACH_SPEED = 1.2F;
    private static final int MELEE_ATTACK_INTERVAL = 20;
    public static final int ROAR_DURATION = Mth.ceil(20.0F);
    private static final int SNIFF_DURATION = Mth.ceil(83.2F);

    private static final List<SensorType<? extends Sensor<? super BloaterEntity>>> SENSORS;
    private static final List<MemoryModuleType<?>> MEMORY_MODULES;

    public BloaterBrain() {
    }

    public static void updateActivities(BloaterEntity bloater) {
        bloater.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.ROAR, Activity.FIGHT, Activity.INVESTIGATE, Activity.SNIFF, Activity.IDLE));
    }

    protected static Brain<?> create(BloaterEntity bloater, Dynamic<?> dynamic) {
        Brain.Provider<BloaterEntity> provider = Brain.provider(MEMORY_MODULES, SENSORS);
        Brain<BloaterEntity> brain = provider.makeBrain(dynamic);
        addCoreActivities(brain);
        addIdleActivities(brain);
        addRoarActivities(brain);
        addFightActivities(bloater, brain);
        addInvestigateActivities(brain);
        addSniffActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    static ImmutableList<BehaviorControl<? super BloaterEntity>> idleTasks = ImmutableList.of(
            BloaterFindRoarTargetTask.create(BloaterEntity::getPrimeSuspect),
            TryToSniff.create(),
            new RunOne<>(
                    ImmutableMap.of(
                            MemoryModuleType.IS_SNIFFING,
                            MemoryStatus.VALUE_ABSENT
                    ),
                    ImmutableList.of(
                            Pair.of(RandomStroll.stroll(STROLL_SPEED), 2),
                            Pair.of(new DoNothing(200, 400), 1)
                    )
            )
    );

    private static void addCoreActivities(Brain<BloaterEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim<>(0.8F), SetWardenLookTarget.create(), new LookAtTargetSink(45, 90), new MoveToTargetSink()));
    }
    private static void addIdleActivities(Brain<BloaterEntity> brain) {
        brain.addActivity(Activity.IDLE, 10, idleTasks);
    }

    private static void addInvestigateActivities(Brain<BloaterEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.INVESTIGATE, 5, ImmutableList.of(
                BloaterFindRoarTargetTask.create(BloaterEntity::getPrimeSuspect),
                GoToTargetLocation.create(MemoryModuleType.DISTURBANCE_LOCATION, 2, CELEBRATE_TIME)
        ), MemoryModuleType.DISTURBANCE_LOCATION);
    }

    private static void addSniffActivities(Brain<BloaterEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.SNIFF, 5, ImmutableList.of(
                BloaterFindRoarTargetTask.create(BloaterEntity::getPrimeSuspect),
                new BloaterSniffTask<>(SNIFF_DURATION)
        ), MemoryModuleType.IS_SNIFFING);
    }

    private static void addRoarActivities(Brain<BloaterEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(
                Activity.ROAR,
                10,
                ImmutableList.of(new BloaterRoarTask()),
                MemoryModuleType.ROAR_TARGET
        );
    }

    private static void addFightActivities(BloaterEntity bloater, Brain<BloaterEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(StopAttackingIfTargetInvalid.create((serverLevel, livingEntity) -> !bloater.getAngriness().isAngry() || !bloater.isValidTarget(livingEntity), BloaterBrain::removeDeadSuspect, false), SetEntityLookTarget.create((livingEntity) -> isTargeting(bloater, livingEntity), (float)bloater.getAttributeValue(Attributes.FOLLOW_RANGE)), SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.2F), new ThrowTask(), MeleeAttack.create(18)), MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean isTargeting(BloaterEntity bloater, LivingEntity entity) {
        return bloater.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter((entityx) -> entityx == entity).isPresent();
    }

    public static void lookAtDisturbance(BloaterEntity bloater, BlockPos pos) {
        if (bloater.level().getWorldBorder().isWithinBounds(pos) && bloater.getPrimeSuspect().isEmpty() && bloater.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()) {
            bloater.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 100L);
            bloater.getBrain().setMemoryWithExpiry(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos), 100L);
            bloater.getBrain().setMemoryWithExpiry(MemoryModuleType.DISTURBANCE_LOCATION, pos, 100L);
            bloater.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
    }

    private static void removeDeadSuspect(ServerLevel world, BloaterEntity bloater, LivingEntity target) {
        if (!bloater.isValidTarget(target)) {
            bloater.removeSuspect(target);
        }
    }

    static {
        SENSORS = List.of(SensorType.NEAREST_PLAYERS, ModSensors.BLOATER_ENTITY_SENSOR);
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
                MemoryModuleType.VIBRATION_COOLDOWN,
                MemoryModuleType.SONIC_BOOM_COOLDOWN,
                MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN,
                MemoryModuleType.SONIC_BOOM_SOUND_DELAY
        );
    }
}
