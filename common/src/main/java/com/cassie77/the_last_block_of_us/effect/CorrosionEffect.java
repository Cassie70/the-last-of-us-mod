package com.cassie77.the_last_block_of_us.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Works like vanilla poison (1 damage every 3 ticks, halved per amplifier level)
 * but, unlike poison, it does not stop at 1 health: it can kill the entity.
 */
public class CorrosionEffect extends MobEffect {

    public static final int DAMAGE_INTERVAL = 3;
    private static final float DAMAGE_PER_APPLICATION = 1.0F;

    public CorrosionEffect() {
        super(MobEffectCategory.HARMFUL, 0xDEE807);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level() instanceof ServerLevel level
                && entity.hurt(level.damageSources().magic(), DAMAGE_PER_APPLICATION)) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 0.6F, 1.0F);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % DAMAGE_INTERVAL == 0;
    }
}