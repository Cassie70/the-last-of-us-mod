package com.cassie77.entity.bloater;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.server.world.ServerWorld;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BloaterAttackablesSensor extends NearestLivingEntitiesSensor<BloaterEntity> {
    public BloaterAttackablesSensor() {
    }

    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.copyOf(Iterables.concat(super.getOutputMemoryModules(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    protected void sense(ServerWorld serverWorld, BloaterEntity BloaterEntity) {
        super.sense(serverWorld, BloaterEntity);
        findNearestTarget(BloaterEntity, (entityx) -> entityx.getType() == EntityType.PLAYER).or(() -> findNearestTarget(BloaterEntity, (entityx) -> entityx.getType() != EntityType.PLAYER)).ifPresentOrElse((entityx) -> BloaterEntity.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, entityx), () -> BloaterEntity.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE));
    }

    private static Optional<LivingEntity> findNearestTarget(BloaterEntity clicker, Predicate<LivingEntity> targetPredicate) {
        Stream<LivingEntity> var10000 = clicker.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS).stream().flatMap(Collection::stream);
        Objects.requireNonNull(clicker);
        return var10000.filter(clicker::isValidTarget).filter(targetPredicate).findFirst();
    }
}
