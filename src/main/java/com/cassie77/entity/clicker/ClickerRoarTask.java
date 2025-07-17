package com.cassie77.entity.clicker;

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

public class ClickerRoarTask extends MultiTickTask<ClickerEntity> {
    private static final int SOUND_DELAY = 0;
    private static final int ANGER_INCREASE = 20;

    public ClickerRoarTask() {
        super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryModuleState.REGISTERED, MemoryModuleType.ROAR_SOUND_DELAY, MemoryModuleState.REGISTERED), ClickerBrain.ROAR_DURATION);
    }

    protected void run(ServerWorld serverWorld, ClickerEntity clickerEntity, long l) {
        Brain<ClickerEntity> brain = clickerEntity.getBrain();
        brain.remember(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, SOUND_DELAY);
        brain.forget(MemoryModuleType.WALK_TARGET);
        LivingEntity livingEntity = clickerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ROAR_TARGET).get();
        TargetUtil.lookAt(clickerEntity, livingEntity);
        clickerEntity.setPose(EntityPose.ROARING);
        clickerEntity.increaseAngerAt(livingEntity, ANGER_INCREASE, false);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, ClickerEntity clickerEntity, long l) {
        return true;
    }

    protected void keepRunning(ServerWorld serverWorld, ClickerEntity clickerEntity, long l) {
        if (!clickerEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_SOUND_DELAY) && !clickerEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
            clickerEntity.getBrain().remember(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE, ClickerBrain.ROAR_DURATION - SOUND_DELAY);
            clickerEntity.playSound(ModSounds.CLICKER_ANGRY, 5.0F, 1.0F);
        }
    }

    protected void finishRunning(ServerWorld serverWorld, ClickerEntity clickerEntity, long l) {
        if (clickerEntity.isInPose(EntityPose.ROARING)) {
            clickerEntity.setPose(EntityPose.STANDING);
        }

        Optional<LivingEntity> var10000 = clickerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ROAR_TARGET);
        Objects.requireNonNull(clickerEntity);
        var10000.ifPresent(clickerEntity::updateAttackTarget);
        clickerEntity.getBrain().forget(MemoryModuleType.ROAR_TARGET);
    }
}
