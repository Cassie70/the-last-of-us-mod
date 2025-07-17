package com.cassie77.entity.clicker;

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

public class ClickerAttackablesSensor extends NearestLivingEntitiesSensor<ClickerEntity> {
    public ClickerAttackablesSensor() {
    }

    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.copyOf(Iterables.concat(super.getOutputMemoryModules(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    protected void sense(ServerWorld serverWorld, ClickerEntity clickerEntity) {
        super.sense(serverWorld, clickerEntity);
        findNearestTarget(clickerEntity, (entityx) -> entityx.getType() == EntityType.PLAYER).or(() -> findNearestTarget(clickerEntity, (entityx) -> entityx.getType() != EntityType.PLAYER)).ifPresentOrElse((entityx) -> clickerEntity.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, entityx), () -> clickerEntity.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE));
    }

    private static Optional<LivingEntity> findNearestTarget(ClickerEntity clicker, Predicate<LivingEntity> targetPredicate) {
        Stream<LivingEntity> var10000 = clicker.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS).stream().flatMap(Collection::stream);
        Objects.requireNonNull(clicker);
        return var10000.filter(clicker::isValidTarget).filter(targetPredicate).findFirst();
    }
}
