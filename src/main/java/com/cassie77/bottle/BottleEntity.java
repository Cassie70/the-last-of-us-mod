package com.cassie77.bottle;

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
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class BottleEntity extends ThrownItemEntity {

    public BottleEntity(EntityType<? extends BottleEntity> entityType, World world) {
        super(entityType, world);
    }

    public BottleEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.BOTTLE_ENTITY, owner, world, stack);
    }

    public BottleEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.BOTTLE_ENTITY, x, y, z, world, stack);
    }

    protected Item getDefaultItem() {
        return ModItems.BOTTLE;
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getStack();
        return (ParticleEffect)(itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for(int i = 0; i < 8; ++i) {
                this.getWorld().addParticleClient(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F, 0.0F);
            }
        }

    }
    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.getWorld().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_SPLASH_POTION_BREAK,
                    SoundCategory.NEUTRAL,
                    2.0F, 1.0F
            );
            this.discard();
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
