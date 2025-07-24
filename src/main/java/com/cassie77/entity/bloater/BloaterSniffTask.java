package com.cassie77.entity.bloater;

import com.cassie77.ModSounds;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;

import java.util.Objects;
import java.util.Optional;

public class BloaterSniffTask<E extends BloaterEntity> extends MultiTickTask<E> {
    private static final double HORIZONTAL_RADIUS = 6.0F;
    private static final double VERTICAL_RADIUS = 20.0F;

    public BloaterSniffTask(int runTime) {
        super(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleState.REGISTERED, MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleState.REGISTERED, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleState.REGISTERED), runTime);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, E BloaterEntity, long l) {
        return true;
    }

    protected void run(ServerWorld serverWorld, E BloaterEntity, long l) {
        BloaterEntity.playSound(ModSounds.BLOATER_AWARE, 2.5F, 1.0F);
    }

    protected void finishRunning(ServerWorld serverWorld, E BloaterEntity, long l) {
        if (BloaterEntity.isInPose(EntityPose.SNIFFING)) {
            BloaterEntity.setPose(EntityPose.STANDING);
        }

        BloaterEntity.getBrain().forget(MemoryModuleType.IS_SNIFFING);
        Optional<LivingEntity> var10000 = BloaterEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        Objects.requireNonNull(BloaterEntity);
        var10000.filter(BloaterEntity::isValidTarget).ifPresent((target) -> {
            if (BloaterEntity.isInRange(target, HORIZONTAL_RADIUS, VERTICAL_RADIUS)) {
                BloaterEntity.increaseAngerAt(target);
            }

            if (!BloaterEntity.getBrain().hasMemoryModule(MemoryModuleType.DISTURBANCE_LOCATION)) {
                BloaterBrain.lookAtDisturbance(BloaterEntity, target.getBlockPos());
            }

        });
    }
}
