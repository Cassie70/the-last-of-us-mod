package com.cassie77.item.nailbomb;

import com.cassie77.ModEntities;
import com.cassie77.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class NailBombEntity extends ThrownItemEntity {

    private int ticksOnGround = 0;

    public NailBombEntity(EntityType<? extends NailBombEntity> entityType, World world) {
        super(entityType, world);
    }

    public NailBombEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.NAIL_BOMB_ENTITY, owner, world, stack);
    }

    public NailBombEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.NAIL_BOMB_ENTITY, x, y, z, world, stack);
    }

    protected Item getDefaultItem() {
        return ModItems.NAIL_BOMB;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte)3);

            this.setVelocity(0, 0, 0);
            this.velocityDirty = true;


            ticksOnGround++;
            if (ticksOnGround > 60) {
                this.setPos(this.getX(), this.getBlockY() + 2, this.getZ());
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                ((ServerWorld) this.getWorld()).spawnParticles(
                        ParticleTypes.EXPLOSION,
                        this.getX() + 0.5,
                        this.getY() + 0.5,
                        this.getZ() + 0.5,
                        50, // count
                        2, 2, 2,// offset X,Y,Z
                        0.0 // speed
                );


                this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 5.0F, false, World.ExplosionSourceType.NONE);
                this.discard();
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.getWorld().isClient) {
            Entity entity = entityHitResult.getEntity();

            float damageAmount = 5.0F;
            if(this.getWorld() instanceof ServerWorld serverWorld)
                entity.damage(serverWorld,this.getWorld().getDamageSources().generic(), damageAmount);

            if(entity instanceof LivingEntity livingEntity){
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 7, false, true));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 5, false, true));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 5, false, true));
            }
        }
    }
}
