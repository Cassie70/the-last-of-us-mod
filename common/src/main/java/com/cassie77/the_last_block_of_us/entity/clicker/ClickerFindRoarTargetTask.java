package com.cassie77.the_last_block_of_us.entity.clicker;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ClickerFindRoarTargetTask {

    ClickerFindRoarTargetTask() {}

    public static <E extends ClickerEntity> BehaviorControl<E> create(Function<E, Optional<? extends LivingEntity>> targetFinder) {
        return BehaviorBuilder.create((context) -> context.group(
                context.absent(MemoryModuleType.ROAR_TARGET),
                context.absent(MemoryModuleType.ATTACK_TARGET),
                context.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
        ).apply(context, (roarTarget, attackTarget, cantReachWalkTargetSince) -> (world, entity, time) -> {
            Optional<? extends LivingEntity> optional = targetFinder.apply(entity);
            Objects.requireNonNull(entity);
            if (optional.filter(entity::isValidTarget).isEmpty()) {
                return false;
            } else {
                roarTarget.set(optional.get());
                cantReachWalkTargetSince.erase();
                return true;
            }
        }));
    }
}
