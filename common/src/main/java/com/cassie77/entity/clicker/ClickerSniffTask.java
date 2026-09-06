package com.cassie77.entity.clicker;

import com.cassie77.ModSounds;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Objects;
import java.util.Optional;

public class ClickerSniffTask<E extends ClickerEntity> extends Behavior<E> {
    private static final double HORIZONTAL_RADIUS = 4.0;
    private static final double VERTICAL_RADIUS = 4.0;

    public ClickerSniffTask(int runTime) {
        super(ImmutableMap.of(
                MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.NEAREST_ATTACKABLE, MemoryStatus.REGISTERED,
                MemoryModuleType.DISTURBANCE_LOCATION, MemoryStatus.REGISTERED,
                MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.REGISTERED
        ), runTime);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, E clickerEntity, long l) {
        return true;
    }

    @Override
    protected void start(ServerLevel serverLevel, E clickerEntity, long l) {
        clickerEntity.playSound(ModSounds.CLICKER_AWARE, 2.0F, 1.0F);
    }

    @Override
    protected void stop(ServerLevel serverLevel, E clickerEntity, long l) {
        if (clickerEntity.hasPose(Pose.SNIFFING)) {
            clickerEntity.setPose(Pose.STANDING);
        }

        clickerEntity.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
        Optional<LivingEntity> nearestAttackable = clickerEntity.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        Objects.requireNonNull(clickerEntity);
        nearestAttackable.filter(clickerEntity::isValidTarget).ifPresent((target) -> {
            if (clickerEntity.closerThan(target, HORIZONTAL_RADIUS, VERTICAL_RADIUS)) {
                clickerEntity.increaseAngerAt(target, 150, false);
            }

            if (!clickerEntity.getBrain().hasMemoryValue(MemoryModuleType.DISTURBANCE_LOCATION)) {
                ClickerBrain.lookAtDisturbance(clickerEntity, target.blockPosition());
            }
        });
    }
}
