package com.cassie77.the_last_block_of_us.entity.bloater;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class BloaterAttackablesSensor extends NearestLivingEntitySensor<BloaterEntity> {

    public BloaterAttackablesSensor() {
    }

    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    protected void doTick(ServerLevel serverLevel, BloaterEntity bloater) {
        super.doTick(serverLevel, bloater);
        getClosest(bloater, (livingEntity) -> livingEntity.getType() == EntityType.PLAYER).or(() -> getClosest(bloater, (livingEntity) -> livingEntity.getType() != EntityType.PLAYER)).ifPresentOrElse((livingEntity) -> bloater.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, livingEntity), () -> bloater.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE));
    }


    private static Optional<LivingEntity> getClosest(BloaterEntity bloater, Predicate<LivingEntity> predicate) {
        Stream<LivingEntity> var10000 = bloater.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().flatMap(Collection::stream);
        Objects.requireNonNull(bloater);
        return var10000.filter(bloater::isValidTarget).filter(predicate).findFirst();
    }
}
