package com.cassie77.entity.bloater;

import com.cassie77.ModSounds;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.TargetUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Objects;
import java.util.Optional;

public class BloaterRoarTask extends MultiTickTask<BloaterEntity> {
    private static final int SOUND_DELAY = 0;
    private static final int ANGER_INCREASE = 20;

    public BloaterRoarTask() {
        super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryModuleState.REGISTERED, MemoryModuleType.ROAR_SOUND_DELAY, MemoryModuleState.REGISTERED), BloaterBrain.ROAR_DURATION);
    }

    protected void run(ServerWorld serverWorld, BloaterEntity BloaterEntity, long l) {
        Brain<BloaterEntity> brain = BloaterEntity.getBrain();
        brain.remember(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, SOUND_DELAY);
        brain.forget(MemoryModuleType.WALK_TARGET);
        LivingEntity livingEntity = BloaterEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ROAR_TARGET).get();
        TargetUtil.lookAt(BloaterEntity, livingEntity);
        BloaterEntity.setPose(EntityPose.ROARING);
        BloaterEntity.increaseAngerAt(livingEntity, ANGER_INCREASE, false);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, BloaterEntity BloaterEntity, long l) {
        return true;
    }

    protected void keepRunning(ServerWorld serverWorld, BloaterEntity BloaterEntity, long l) {
        if (!BloaterEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_SOUND_DELAY) && !BloaterEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
            BloaterEntity.getBrain().remember(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE, BloaterBrain.ROAR_DURATION - SOUND_DELAY);
            BloaterEntity.playSound(ModSounds.BLOATER_ANGRY, 2.5F, 1.0F);
        }
    }

    protected void finishRunning(ServerWorld serverWorld, BloaterEntity BloaterEntity, long l) {
        if (BloaterEntity.isInPose(EntityPose.ROARING)) {
            BloaterEntity.setPose(EntityPose.STANDING);
        }

        Optional<LivingEntity> var10000 = BloaterEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ROAR_TARGET);
        Objects.requireNonNull(BloaterEntity);
        var10000.ifPresent(BloaterEntity::updateAttackTarget);
        BloaterEntity.getBrain().forget(MemoryModuleType.ROAR_TARGET);
    }
}
