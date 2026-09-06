package com.cassie77.the_last_block_of_us.item.bottle;

import com.cassie77.the_last_block_of_us.ModEntities;
import com.cassie77.the_last_block_of_us.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class BottleEntity extends ThrowableItemProjectile {

    public BottleEntity(EntityType<? extends BottleEntity> entityType, Level world) {
        super(entityType, world);
    }

    public BottleEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.BOTTLE_ENTITY, owner, world, stack);
    }

    public BottleEntity(Level world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.BOTTLE_ENTITY, x, y, z, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BOTTLE;
    }

    private ParticleOptions getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, itemStack);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 3) {
            ParticleOptions particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.level().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.SPLASH_POTION_BREAK,
                    SoundSource.NEUTRAL,
                    2.0F, 1.0F
            );
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);

        if (!this.level().isClientSide()) {
            Entity entity = entityHitResult.getEntity();

            float damageAmount = 5.0F;
            if (this.level() instanceof ServerLevel serverWorld) {
                entity.hurtServer(serverWorld, this.damageSources().generic(), damageAmount);
            }

            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 7, false, true));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 5, false, true));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 5, false, true));
            }
        }
    }
}
