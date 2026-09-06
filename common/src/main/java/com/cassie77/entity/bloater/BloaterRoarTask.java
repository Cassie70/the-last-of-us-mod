package com.cassie77.entity.bloater;

import com.cassie77.ModSounds;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Objects;
import java.util.Optional;

public class BloaterRoarTask extends Behavior<BloaterEntity> {
    private static final int SOUND_DELAY = 0;
    private static final int ANGER_INCREASE = 20;

    public BloaterRoarTask() {
        super(ImmutableMap.of(
                MemoryModuleType.ROAR_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryStatus.REGISTERED,
                MemoryModuleType.ROAR_SOUND_DELAY, MemoryStatus.REGISTERED), BloaterBrain.ROAR_DURATION);
    }

    @Override
    protected void start(ServerLevel serverLevel, BloaterEntity bloaterEntity, long l) {
        Brain<BloaterEntity> brain = bloaterEntity.getBrain();
        brain.setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, SOUND_DELAY);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        LivingEntity livingEntity = bloaterEntity.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).get();
        BehaviorUtils.lookAtEntity(bloaterEntity, livingEntity);
        bloaterEntity.setPose(Pose.ROARING);
        bloaterEntity.increaseAngerAt(livingEntity, ANGER_INCREASE, false);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, BloaterEntity bloaterEntity, long l) {
        return true;
    }

    @Override
    protected void tick(ServerLevel serverLevel, BloaterEntity bloaterEntity, long l) {
        if (!bloaterEntity.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_DELAY)
                && !bloaterEntity.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
            bloaterEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE,
                    BloaterBrain.ROAR_DURATION - SOUND_DELAY);
            bloaterEntity.playSound(ModSounds.BLOATER_ANGRY, 2.5F, 1.0F);
        }
    }

    @Override
    protected void stop(ServerLevel serverLevel, BloaterEntity bloaterEntity, long l) {
        if (bloaterEntity.hasPose(Pose.ROARING)) {
            bloaterEntity.setPose(Pose.STANDING);
        }

        Optional<LivingEntity> roarTarget = bloaterEntity.getBrain().getMemory(MemoryModuleType.ROAR_TARGET);
        Objects.requireNonNull(bloaterEntity);
        roarTarget.ifPresent(bloaterEntity::updateAttackTarget);
        bloaterEntity.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
    }
}
