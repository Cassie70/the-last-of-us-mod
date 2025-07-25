package com.cassie77.entity.bloater;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class BloaterFindRoarTargetTask {

    BloaterFindRoarTargetTask(){}

    public static <E extends BloaterEntity> Task<E> create(Function<E, Optional<? extends LivingEntity>> targetFinder) {
        return TaskTriggerer.task((context) -> context.group(context.queryMemoryAbsent(MemoryModuleType.ROAR_TARGET), context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(context, (roarTarget, attackTarget, cantReachWalkTargetSince) -> (world, entity, time) -> {
            Optional<? extends LivingEntity> optional = targetFinder.apply(entity);
            Objects.requireNonNull(entity);
            if (optional.filter(entity::isValidTarget).isEmpty()) {
                return false;
            } else {
                roarTarget.remember(optional.get());
                cantReachWalkTargetSince.forget();
                return true;
            }
        }));
    }
}
