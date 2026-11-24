package com.cassie77.the_last_block_of_us.item.smokebomb;

import com.cassie77.the_last_block_of_us.ModEntities;
import com.cassie77.the_last_block_of_us.ModItems;
import com.cassie77.the_last_block_of_us.entity.CustomAreaEffectCloudEntity;
import com.cassie77.the_last_block_of_us.item.AbstractBombEntity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

public class SmokeBombEntity extends AbstractBombEntity {

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
    protected @NotNull Item getDefaultItem() {
        return ModItems.SMOKE_BOMB;
    }

    @Override
    protected float getDirectHitDamage() {
        return 1.0F;
    }

    @Override
    protected void applyDirectHitEffects(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 7, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 5, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 5, false, true));
    }

    @Override
    protected void onDetonate(ServerLevel serverLevel) {
        double expY = this.getY() + 0.5;

        serverLevel.playSound(null, this.getX(), expY, this.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 1.0F, 1.0F);

        serverLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                this.getX(),
                expY,
                this.getZ(),
                25,
                2.0, 2.0, 2.0,
                0.0
        );

        this.spawnAreaEffectCloud(serverLevel);
    }

    public void spawnAreaEffectCloud(ServerLevel world) {
        CustomAreaEffectCloudEntity cloud = new CustomAreaEffectCloudEntity(
                this.level(), this.getX(), this.getY() + 0.1, this.getZ()
        );
        if (this.getOwner() instanceof LivingEntity livingEntity) {
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
        cloud.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, true));
        cloud.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, true));
        cloud.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, true));
        world.addFreshEntity(cloud);
    }
}
