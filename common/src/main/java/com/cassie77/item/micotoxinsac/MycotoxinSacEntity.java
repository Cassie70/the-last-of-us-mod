package com.cassie77.item.micotoxinsac;

import com.cassie77.ModEntities;
import com.cassie77.ModItems;
import com.cassie77.entity.CustomAreaEffectCloudEntity;
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

public class MycotoxinSacEntity extends ThrowableItemProjectile {

    public static final float RADIUS = 3.0F;

    public MycotoxinSacEntity(EntityType<? extends MycotoxinSacEntity> entityType, Level world) {
        super(entityType, world);
    }

    public MycotoxinSacEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.MYCOTOXIN_SAC_ENTITY, owner, world, stack);
    }

    public MycotoxinSacEntity(Level world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.MYCOTOXIN_SAC_ENTITY, x, y, z, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MYCOTOXIN_SAC;
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

        Level world = this.level();

        if (world instanceof ServerLevel serverWorld) {
            serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);

            serverWorld.explode(this, this.getX(), this.getY(), this.getZ(), 1.5F, Level.ExplosionInteraction.NONE);

            this.spawnAreaEffectCloud(serverWorld);

            this.discard();
        }
    }

    public void spawnAreaEffectCloud(ServerLevel world) {
        CustomAreaEffectCloudEntity cloud = new CustomAreaEffectCloudEntity(this.level(), this.getX(), this.getY(), this.getZ());
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity livingEntity) {
            cloud.setOwner(livingEntity);
        }

        cloud.setParticleArea(ParticleTypes.FIREFLY);
        cloud.setDensityFactor(0.2F);
        cloud.setRadius(RADIUS);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setDuration(600);
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        cloud.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
        world.addFreshEntity(cloud);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);

        if (!this.level().isClientSide()) {
            Entity entity = entityHitResult.getEntity();

            float damageAmount = 1.0F;
            if (this.level() instanceof ServerLevel serverWorld) {
                entity.hurtServer(serverWorld, this.damageSources().generic(), damageAmount);
            }

            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1, false, true));
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            ParticleOptions particleEffect = ParticleTypes.SMOKE;
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(particleEffect, this.getX() + this.getRandom().nextGaussian() * 0.2, this.getY() + this.getRandom().nextGaussian() * 0.2, this.getZ() + this.getRandom().nextGaussian() * 0.2, 0.0, 0.0, 0.0);
            }
        }
    }
}
