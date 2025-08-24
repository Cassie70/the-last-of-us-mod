package com.cassie77.entity.clicker;

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

public class ClickerSniffTask<E extends ClickerEntity> extends MultiTickTask<E> {
    private static final double HORIZONTAL_RADIUS = 4.0F;
    private static final double VERTICAL_RADIUS = 4.0F;

    public ClickerSniffTask(int runTime) {
        super(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleState.REGISTERED, MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleState.REGISTERED, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleState.REGISTERED), runTime);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, E clickerEntity, long l) {
        return true;
    }

    protected void run(ServerWorld serverWorld, E clickerEntity, long l) {
        clickerEntity.playSound(ModSounds.CLICKER_AWARE, 2.5F, 1.0F);
    }

    protected void finishRunning(ServerWorld serverWorld, E clickerEntity, long l) {
        if (clickerEntity.isInPose(EntityPose.SNIFFING)) {
            clickerEntity.setPose(EntityPose.STANDING);
        }

        clickerEntity.getBrain().forget(MemoryModuleType.IS_SNIFFING);
        Optional<LivingEntity> var10000 = clickerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        Objects.requireNonNull(clickerEntity);
        var10000.filter(clickerEntity::isValidTarget).ifPresent((target) -> {
            if (clickerEntity.isInRange(target, HORIZONTAL_RADIUS, VERTICAL_RADIUS)) {
                clickerEntity.increaseAngerAt(target, 150, false);
            }

            if (!clickerEntity.getBrain().hasMemoryModule(MemoryModuleType.DISTURBANCE_LOCATION)) {
                ClickerBrain.lookAtDisturbance(clickerEntity, target.getBlockPos());
            }

        });
    }
}
