package com.cassie77.the_last_block_of_us.entity.clicker;

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

public class ClickerAttackablesSensor extends NearestLivingEntitySensor<ClickerEntity> {

    public ClickerAttackablesSensor() {
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    protected void doTick(ServerLevel serverLevel, ClickerEntity clicker) {
        super.doTick(serverLevel, clicker);
        getClosest(clicker, (livingEntity) -> livingEntity.getType() == EntityType.PLAYER).or(() -> getClosest(clicker, (livingEntity) -> livingEntity.getType() != EntityType.PLAYER)).ifPresentOrElse((livingEntity) -> clicker.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, livingEntity), () -> clicker.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE));
    }

    private static Optional<LivingEntity> getClosest(ClickerEntity clicker, Predicate<LivingEntity> predicate) {
        Stream<LivingEntity> var10000 = clicker.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().flatMap(Collection::stream);
        Objects.requireNonNull(clicker);
        return var10000.filter(clicker::isValidTarget).filter(predicate).findFirst();
    }
}
