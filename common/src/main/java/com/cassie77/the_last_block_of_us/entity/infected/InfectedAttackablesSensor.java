package com.cassie77.the_last_block_of_us.entity.infected;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class InfectedAttackablesSensor extends NearestLivingEntitySensor<InfectedEntity> {

    public InfectedAttackablesSensor() {
    }

    @Override
    public @NotNull Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    protected void doTick(@NotNull ServerLevel serverLevel, @NotNull InfectedEntity infected) {
        super.doTick(serverLevel, infected);
        getClosest(infected, (livingEntity) -> livingEntity.getType() == EntityType.PLAYER).or(() -> getClosest(infected, (livingEntity) -> livingEntity.getType() != EntityType.PLAYER)).ifPresentOrElse((livingEntity) -> infected.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, livingEntity), () -> infected.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE));
    }

    private static Optional<LivingEntity> getClosest(InfectedEntity infected, Predicate<LivingEntity> predicate) {
        Stream<LivingEntity> var10000 = infected.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().flatMap(Collection::stream);
        Objects.requireNonNull(infected);
        return var10000.filter(infected::isValidTarget).filter(predicate).findFirst();
    }
}
