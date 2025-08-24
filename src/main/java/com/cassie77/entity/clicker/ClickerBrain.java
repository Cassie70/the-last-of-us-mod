package com.cassie77.entity.clicker;

import com.cassie77.ModSensors;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class ClickerBrain {

    private static final float STROLL_SPEED = 0.5F;
    private static final float CELEBRATE_TIME = 0.7F;
    private static final float RANGED_APPROACH_SPEED = 1.2F;
    private static final int MELEE_ATTACK_INTERVAL = 18;
    public static final int ROAR_DURATION = MathHelper.ceil(20.0F);
    private static final int SNIFF_DURATION = MathHelper.ceil(83.2F);

    private static final List<SensorType<? extends Sensor<? super ClickerEntity>>> SENSORS;
    private static final List<MemoryModuleType<?>> MEMORY_MODULES;

    public ClickerBrain() {
    }

    public static void updateActivities(ClickerEntity clicker) {
        clicker.getBrain().resetPossibleActivities(ImmutableList.of(Activity.ROAR, Activity.FIGHT, Activity.INVESTIGATE, Activity.SNIFF, Activity.IDLE));
    }

    protected static Brain<?> create(ClickerEntity clicker, Dynamic<?> dynamic) {
        Brain.Profile<ClickerEntity> profile = Brain.createProfile(MEMORY_MODULES, SENSORS);
        Brain<ClickerEntity> brain = profile.deserialize(dynamic);
        addCoreActivities(brain);
        addIdleActivities(brain);
        addRoarActivities(brain);
        addFightActivities(clicker, brain);
        addInvestigateActivities(brain);
        addSniffActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    static ImmutableList<Task<? super ClickerEntity>> idleTasks = ImmutableList.of(
            ClickerFindRoarTargetTask.create(ClickerEntity::getPrimeSuspect),
            StartSniffingTask.create(),
            new RandomTask<>(
                    ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryModuleState.VALUE_ABSENT),
                    ImmutableList.of(
                            Pair.of(StrollTask.create(STROLL_SPEED), 2),
                            Pair.of(new WaitTask(60, 200), 1)
                    )
            )
    );

    private static void addCoreActivities(Brain<ClickerEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask<>(0.8F), LookAtDisturbanceTask.create(), new UpdateLookControlTask(45, 90), new MoveToTargetTask()));
    }

    private static void addIdleActivities(Brain<ClickerEntity> brain) {
        brain.setTaskList(Activity.IDLE, 10, idleTasks);
    }

    private static void addInvestigateActivities(Brain<ClickerEntity> brain) {
        brain.setTaskList(Activity.INVESTIGATE, 5, ImmutableList.of(ClickerFindRoarTargetTask.create(ClickerEntity::getPrimeSuspect), WalkTowardsFuzzyPosTask.create(MemoryModuleType.DISTURBANCE_LOCATION, 2, CELEBRATE_TIME)), MemoryModuleType.DISTURBANCE_LOCATION);
    }

    private static void addSniffActivities(Brain<ClickerEntity> brain) {
        brain.setTaskList(Activity.SNIFF, 5, ImmutableList.of(ClickerFindRoarTargetTask.create(ClickerEntity::getPrimeSuspect), new ClickerSniffTask<>(SNIFF_DURATION)), MemoryModuleType.IS_SNIFFING);
    }

    private static void addRoarActivities(Brain<ClickerEntity> brain) {
        // Lista de tareas, solo con tareas
        ImmutableList<Task<? super ClickerEntity>> roarTasks = ImmutableList.of(
                new ClickerRoarTask()
        );

        brain.setTaskList(
                Activity.ROAR,
                10,
                roarTasks,
                MemoryModuleType.ROAR_TARGET
        );
    }

    private static void addFightActivities(ClickerEntity clicker, Brain<ClickerEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 10, ImmutableList.of(ForgetAttackTargetTask.create((world, target) -> !clicker.getAngriness().isAngry() || !clicker.isValidTarget(target), ClickerBrain::removeDeadSuspect, false), LookAtMobTask.create((entity) -> isTargeting(clicker, entity), (float)clicker.getAttributeValue(EntityAttributes.FOLLOW_RANGE)), RangedApproachTask.create(RANGED_APPROACH_SPEED), MeleeAttackTask.create(MELEE_ATTACK_INTERVAL)), MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean isTargeting(ClickerEntity clicker, LivingEntity entity) {
        return clicker.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).filter((entityx) -> entityx == entity).isPresent();
    }

    private static void removeDeadSuspect(ServerWorld world, ClickerEntity clicker, LivingEntity target) {
        if (!clicker.isValidTarget(target)) {
            clicker.removeSuspect(target);
        }
    }

    public static void lookAtDisturbance(ClickerEntity clicker, BlockPos pos) {
        if (clicker.getWorld().getWorldBorder().contains(pos) && clicker.getPrimeSuspect().isEmpty() && clicker.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()) {
            clicker.getBrain().remember(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 100L);
            clicker.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(pos), 100L);
            clicker.getBrain().remember(MemoryModuleType.DISTURBANCE_LOCATION, pos, 100L);
            clicker.getBrain().forget(MemoryModuleType.WALK_TARGET);
        }
    }

    static {
        SENSORS = List.of(SensorType.NEAREST_PLAYERS, ModSensors.CLICKER_ENTITY_SENSOR);
        MEMORY_MODULES = List.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.ROAR_TARGET, MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleType.RECENT_PROJECTILE, MemoryModuleType.IS_SNIFFING, MemoryModuleType.IS_EMERGING, MemoryModuleType.ROAR_SOUND_DELAY, MemoryModuleType.DIG_COOLDOWN, MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleType.TOUCH_COOLDOWN, MemoryModuleType.VIBRATION_COOLDOWN);
    }
}
