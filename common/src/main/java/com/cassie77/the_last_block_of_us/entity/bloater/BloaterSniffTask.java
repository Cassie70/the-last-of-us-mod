package com.cassie77.the_last_block_of_us.entity.bloater;

import com.cassie77.the_last_block_of_us.ModSounds;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Objects;
import java.util.Optional;

public class BloaterSniffTask<E extends BloaterEntity> extends Behavior<E> {
    private static final double HORIZONTAL_RADIUS = 3.0;
    private static final double VERTICAL_RADIUS = 15.0;

    public BloaterSniffTask(int runTime) {
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
    protected boolean canStillUse(ServerLevel serverLevel, E bloaterEntity, long l) {
        return true;
    }

    @Override
    protected void start(ServerLevel serverLevel, E bloaterEntity, long l) {
        bloaterEntity.playSound(ModSounds.BLOATER_AWARE, 2.5F, 1.0F);
    }

    @Override
    protected void stop(ServerLevel serverLevel, E bloaterEntity, long l) {
        if (bloaterEntity.hasPose(Pose.SNIFFING)) {
            bloaterEntity.setPose(Pose.STANDING);
        }

        bloaterEntity.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
        Optional<LivingEntity> nearestAttackable = bloaterEntity.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        Objects.requireNonNull(bloaterEntity);
        nearestAttackable.filter(bloaterEntity::isValidTarget).ifPresent((target) -> {
            if (bloaterEntity.closerThan(target, HORIZONTAL_RADIUS, VERTICAL_RADIUS)) {
                bloaterEntity.increaseAngerAt(target, 150, false);
            }

            if (!bloaterEntity.getBrain().hasMemoryValue(MemoryModuleType.DISTURBANCE_LOCATION)) {
                BloaterBrain.lookAtDisturbance(bloaterEntity, target.blockPosition());
            }
        });
    }
}
