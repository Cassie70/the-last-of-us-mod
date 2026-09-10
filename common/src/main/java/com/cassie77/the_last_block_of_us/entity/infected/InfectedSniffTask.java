package com.cassie77.the_last_block_of_us.entity.infected;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class InfectedSniffTask<E extends InfectedEntity> extends Behavior<E> {
    public InfectedSniffTask(int runTime) {
        super(ImmutableMap.of(
                MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.NEAREST_ATTACKABLE, MemoryStatus.REGISTERED,
                MemoryModuleType.DISTURBANCE_LOCATION, MemoryStatus.REGISTERED,
                MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.REGISTERED), runTime);
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel serverLevel, @NotNull E infectedEntity, long l) {
        return true;
    }

    @Override
    protected void start(@NotNull ServerLevel serverLevel, E infectedEntity, long l) {
        infectedEntity.playSound(
                infectedEntity.getSniffSound(),
                2.0F,
                1.0F
        );
    }

    @Override
    protected void stop(@NotNull ServerLevel serverLevel, E infectedEntity, long l) {
        if (infectedEntity.hasPose(Pose.SNIFFING)) {
            infectedEntity.setPose(Pose.STANDING);
        }

        infectedEntity.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
        Optional<LivingEntity> nearestAttackable = infectedEntity.getBrain()
                .getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        Objects.requireNonNull(infectedEntity);
        nearestAttackable.filter(infectedEntity::isValidTarget).ifPresent((target) -> {
            if (infectedEntity.closerThan(target, infectedEntity.getInfectedSniffHorizontalRadius(), infectedEntity.getInfectedSniffVerticalRadius())) {
                infectedEntity.increaseAngerAt(target, 150);
            }

            if (!infectedEntity.getBrain().hasMemoryValue(MemoryModuleType.DISTURBANCE_LOCATION)) {
                InfectedBrain.lookAtDisturbance(infectedEntity, target.blockPosition());
            }
        });
    }
}
