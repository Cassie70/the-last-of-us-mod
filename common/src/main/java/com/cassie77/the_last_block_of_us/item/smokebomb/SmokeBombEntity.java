package com.cassie77.the_last_block_of_us.item.smokebomb;

import com.cassie77.the_last_block_of_us.ModEntities;
import com.cassie77.the_last_block_of_us.ModItems;
import com.cassie77.the_last_block_of_us.entity.CustomAreaEffectCloudEntity;
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
import net.minecraft.world.phys.Vec3;

public class SmokeBombEntity extends ThrowableItemProjectile {

    private int ticksOnGround = 0;
    public static final float RADIUS = 4.0F;

    public SmokeBombEntity(EntityType<? extends SmokeBombEntity> entityType, Level world) {
        super(entityType, world);
    }

    public SmokeBombEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.SMOKE_BOMB_ENTITY, owner, world, stack);
    }

    public SmokeBombEntity(Level world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.SMOKE_BOMB_ENTITY, x, y, z, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SMOKE_BOMB;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        Level world = this.level();

        if (world instanceof ServerLevel serverWorld) {
            this.setDeltaMovement(Vec3.ZERO);
            this.hasImpulse = true;

            ticksOnGround++;
            if (ticksOnGround > 60) {
                this.setPos(this.getX(), this.getBlockY() + 2, this.getZ());
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);

                serverWorld.sendParticles(
                        ParticleTypes.EXPLOSION,
                        this.getX() + 0.5,
                        this.getY() + 0.5,
                        this.getZ() + 0.5,
                        25,
                        2, 2, 2,
                        0.0
                );

                this.spawnAreaEffectCloud(serverWorld);
                this.discard();
            }
        }
    }

    public void spawnAreaEffectCloud(ServerLevel world) {
        CustomAreaEffectCloudEntity cloud = new CustomAreaEffectCloudEntity(this.level(), this.getX(), this.getY() - 1, this.getZ());
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity livingEntity) {
            cloud.setOwner(livingEntity);
        }

        cloud.setParticleArea(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE);
        cloud.setDensityFactor(0.075F);
        cloud.setRadius(RADIUS);
        cloud.setRadiusOnUse(-0.1F);
        cloud.setDuration(1000);
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        cloud.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, true));
        cloud.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0, false, true));
        cloud.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, true));
        cloud.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, true));
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
                livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 7, false, true));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 5, false, true));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 5, false, true));
            }
        }
    }
}
