package com.cassie77.the_last_block_of_us.entity.infected;

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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class InfectedRoarTask extends Behavior<InfectedEntity> {
    private static final int SOUND_DELAY = 0;
    private static final int ANGER_INCREASE = 20;

    public InfectedRoarTask(int runTime) {
        super(ImmutableMap.of(
                MemoryModuleType.ROAR_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryStatus.REGISTERED,
                MemoryModuleType.ROAR_SOUND_DELAY, MemoryStatus.REGISTERED), runTime);
    }

    @Override
    protected void start(@NotNull ServerLevel serverLevel, InfectedEntity infectedEntity, long l) {
        Brain<InfectedEntity> brain = infectedEntity.getBrain();
        brain.setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, SOUND_DELAY);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        LivingEntity livingEntity = infectedEntity.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).get();
        BehaviorUtils.lookAtEntity(infectedEntity, livingEntity);
        infectedEntity.setPose(Pose.ROARING);
        infectedEntity.increaseAngerAt(livingEntity, ANGER_INCREASE);
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel serverLevel, @NotNull InfectedEntity infectedEntity, long l) {
        return true;
    }

    @Override
    protected void tick(@NotNull ServerLevel serverLevel, InfectedEntity infectedEntity, long l) {
        if (!infectedEntity.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_DELAY)
                && !infectedEntity.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
            infectedEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE,
                    infectedEntity.getInfectedRoarDuration() - SOUND_DELAY);
            infectedEntity.playSound(infectedEntity.getAngrySound(), 2.0F, 1.0F);
        }
    }

    @Override
    protected void stop(@NotNull ServerLevel serverLevel, InfectedEntity infectedEntity, long l) {
        if (infectedEntity.hasPose(Pose.ROARING)) {
            infectedEntity.setPose(Pose.STANDING);
        }

        Optional<LivingEntity> roarTarget = infectedEntity.getBrain().getMemory(MemoryModuleType.ROAR_TARGET);
        Objects.requireNonNull(infectedEntity);
        roarTarget.ifPresent(infectedEntity::updateAttackTarget);
        infectedEntity.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
    }
}
