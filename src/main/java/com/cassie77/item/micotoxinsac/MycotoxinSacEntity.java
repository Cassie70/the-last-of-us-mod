package com.cassie77.item.micotoxinsac;

import com.cassie77.ModEntities;
import com.cassie77.ModItems;
import com.cassie77.entity.CustomAreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class MycotoxinSacEntity extends ThrownItemEntity {

    public static final float RADIUS = 3.0F;

    public MycotoxinSacEntity(EntityType<? extends MycotoxinSacEntity> entityType, World world) {
        super(entityType, world);
    }

    public MycotoxinSacEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.MYCOTOXIN_SAC_ENTITY, owner, world, stack);
    }

    public MycotoxinSacEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.MYCOTOXIN_SAC_ENTITY, x, y, z, world, stack);
    }

    protected Item getDefaultItem() {
        return ModItems.MYCOTOXIN_SAC;
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getStack();
        return itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack);
    }

    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for(int i = 0; i < 8; ++i) {
                this.getWorld().addParticleClient(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }

    }
    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        World world = this.getWorld();

        if(world instanceof ServerWorld serverWorld) {

            serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 1.0F, 1.0F);

            serverWorld.createExplosion(this, this.getX(), this.getY(), this.getZ(), 1.5F, false, World.ExplosionSourceType.NONE);

            this.spawnAreaEffectCloud(serverWorld);

            this.discard();
        }
    }


    public void spawnAreaEffectCloud(ServerWorld world) {
        CustomAreaEffectCloudEntity cloud = new CustomAreaEffectCloudEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
        Entity var6 = this.getOwner();
        if (var6 instanceof LivingEntity livingEntity) {
            cloud.setOwner(livingEntity);
        }

        cloud.setParticleArea(ParticleTypes.FIREFLY);
        cloud.setDensityFactor(0.1F);
        cloud.setRadius(RADIUS);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setDuration(600);
        cloud.setWaitTime(0);
        cloud.setRadiusGrowth(-cloud.getRadius() / (float)cloud.getDuration());
        cloud.addEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 2));
        world.spawnEntity(cloud);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.getWorld().isClient) {
            Entity entity = entityHitResult.getEntity();

            float damageAmount = 1.0F;
            if(this.getWorld() instanceof ServerWorld serverWorld)
                entity.damage(serverWorld,this.getWorld().getDamageSources().generic(), damageAmount);

            if(entity instanceof LivingEntity livingEntity){
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1, false, true));
            }

        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            ParticleEffect particleEffect = ParticleTypes.SMOKE;
            for (int i = 0; i < 2; ++i) {
                this.getWorld().addParticleClient(particleEffect, this.getX() + this.getRandom().nextGaussian() * 0.2D, this.getY() + this.getRandom().nextGaussian() * 0.2D, this.getZ() + this.getRandom().nextGaussian() * 0.2D, 0.0, 0.0, 0.0);
            }
        }
    }
}
