package com.cassie77.the_last_block_of_us.entity.clicker;

import com.cassie77.the_last_block_of_us.ModSounds;
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

public class ClickerRoarTask extends Behavior<ClickerEntity> {
    private static final int SOUND_DELAY = 0;
    private static final int ANGER_INCREASE = 20;

    public ClickerRoarTask() {
        super(ImmutableMap.of(
                MemoryModuleType.ROAR_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryStatus.REGISTERED,
                MemoryModuleType.ROAR_SOUND_DELAY, MemoryStatus.REGISTERED), ClickerBrain.ROAR_DURATION);
    }

    @Override
    protected void start(ServerLevel serverLevel, ClickerEntity clickerEntity, long l) {
        Brain<ClickerEntity> brain = clickerEntity.getBrain();
        brain.setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, SOUND_DELAY);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        LivingEntity livingEntity = clickerEntity.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).get();
        BehaviorUtils.lookAtEntity(clickerEntity, livingEntity);
        clickerEntity.setPose(Pose.ROARING);
        clickerEntity.increaseAngerAt(livingEntity, ANGER_INCREASE, false);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, ClickerEntity clickerEntity, long l) {
        return true;
    }

    @Override
    protected void tick(ServerLevel serverLevel, ClickerEntity clickerEntity, long l) {
        if (!clickerEntity.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_DELAY)
                && !clickerEntity.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
            clickerEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE,
                    ClickerBrain.ROAR_DURATION - SOUND_DELAY);
            clickerEntity.playSound(ModSounds.CLICKER_ANGRY, 2.0F, 1.0F);
        }
    }

    @Override
    protected void stop(ServerLevel serverLevel, ClickerEntity clickerEntity, long l) {
        if (clickerEntity.hasPose(Pose.ROARING)) {
            clickerEntity.setPose(Pose.STANDING);
        }

        Optional<LivingEntity> roarTarget = clickerEntity.getBrain().getMemory(MemoryModuleType.ROAR_TARGET);
        Objects.requireNonNull(clickerEntity);
        roarTarget.ifPresent(clickerEntity::updateAttackTarget);
        clickerEntity.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
    }
}
