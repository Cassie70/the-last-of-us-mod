package com.cassie77.the_last_block_of_us.entity.infected;

import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class InfectedTryToSniff {

    private static final double SNIFF_RANGE = 6.0;

    private static final IntProvider SNIFF_COOLDOWN = UniformInt.of(100, 200);

    public static BehaviorControl<LivingEntity> create() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.IS_SNIFFING),
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.absent(MemoryModuleType.SNIFF_COOLDOWN),
                instance.present(MemoryModuleType.NEAREST_ATTACKABLE),
                instance.absent(MemoryModuleType.DISTURBANCE_LOCATION))
                .apply(instance, (isSniffing, walkTarget, sniffCooldown, nearestAttackable,
                        disturbance) -> (level, entity, gameTime) -> {

                            LivingEntity target = instance.get(nearestAttackable);

                            if (entity.distanceToSqr(target) > SNIFF_RANGE * SNIFF_RANGE) {
                                return false;
                            }

                            isSniffing.set(Unit.INSTANCE);
                            sniffCooldown.setWithExpiry(
                                    Unit.INSTANCE,
                                    SNIFF_COOLDOWN.sample(entity.getRandom()));

                            walkTarget.erase();
                            entity.setPose(Pose.SNIFFING);

                            return true;
                        }));
    }
}
