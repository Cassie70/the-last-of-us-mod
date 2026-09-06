package com.cassie77.entity;

import com.cassie77.ModEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CustomAreaEffectCloudEntity extends AreaEffectCloud {

    private static final EntityDataAccessor<Float> DENSITY_FACTOR = SynchedEntityData
            .defineId(CustomAreaEffectCloudEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<ParticleOptions> PARTICLE_EFFECT = SynchedEntityData
            .defineId(CustomAreaEffectCloudEntity.class, EntityDataSerializers.PARTICLE);

    public CustomAreaEffectCloudEntity(EntityType<? extends CustomAreaEffectCloudEntity> entityType, Level world) {
        super(entityType, world);
    }

    public CustomAreaEffectCloudEntity(Level world, double x, double y, double z) {
        this(ModEntities.CUSTOM_AREA_EFFECT_CLOUD_ENTITY, world);
        this.setPos(x, y, z);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 3.0F);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide()) {
            super.tick();
        } else {
            this.spawnClientParticles();
        }
    }

    private void spawnClientParticles() {
        float densityFactor = getDensityFactor();

        if (densityFactor <= 0.0F) {
            return;
        }

        ParticleOptions particle = getParticleArea();
        ParticleOptions particleEffect = this.getParticle();

        float radius = this.getRadius();
        int count = Mth.ceil((2.0 / 3.0) * Math.PI * radius * radius * radius * densityFactor);

        double centerX = this.getX();
        double centerY = this.getY();
        double centerZ = this.getZ();

        for (int i = 0; i < count; i++) {
            double xOffset, yOffset, zOffset;
            double length;

            do {
                xOffset = (Math.random() * 2.0 - 1.0);
                yOffset = Math.random();
                zOffset = (Math.random() * 2.0 - 1.0);
                length = xOffset * xOffset + yOffset * yOffset + zOffset * zOffset;
            } while (length > 1.0);

            double norm = 1.0 / Math.sqrt(length);
            xOffset *= norm;
            yOffset *= norm;
            zOffset *= norm;

            double scale = Math.cbrt(Math.random()) * radius;
            xOffset *= scale;
            yOffset *= scale;
            zOffset *= scale;

            double x = centerX + xOffset;
            double y = centerY + yOffset;
            double z = centerZ + zOffset;

            this.level().addAlwaysVisibleParticle(particle, x, y, z, 0.0, 0.0, 0.0);
            this.level().addAlwaysVisibleParticle(particleEffect, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DENSITY_FACTOR, 0.0F);
        builder.define(PARTICLE_EFFECT, ParticleTypes.EFFECT);
    }

    public void setDensityFactor(float densityFactor) {
        this.entityData.set(DENSITY_FACTOR, densityFactor);
    }

    public float getDensityFactor() {
        return this.entityData.get(DENSITY_FACTOR);
    }

    public void setParticleArea(ParticleOptions particle) {
        this.entityData.set(PARTICLE_EFFECT, particle);
    }

    public ParticleOptions getParticleArea() {
        return this.entityData.get(PARTICLE_EFFECT);
    }

    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putFloat("DensityFactor", getDensityFactor());
        valueOutput.store("CustomParticle", ParticleTypes.CODEC, getParticleArea());
    }

    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        setDensityFactor(valueInput.getFloatOr("DensityFactor", 0.0F));
        valueInput.read("CustomParticle", ParticleTypes.CODEC)
                .ifPresent(this::setParticleArea);
    }
}
