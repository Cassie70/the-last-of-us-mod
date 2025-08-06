package com.cassie77.entity;

import com.cassie77.ModEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CustomAreaEffectCloudEntity extends AreaEffectCloudEntity {

    private static final TrackedData<Float> DENSITY_FACTOR =
            DataTracker.registerData(CustomAreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private static final TrackedData<ParticleEffect> PARTICLE_EFFECT =
            DataTracker.registerData(CustomAreaEffectCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);


    public CustomAreaEffectCloudEntity(EntityType<? extends CustomAreaEffectCloudEntity> entityType, World world) {
        super(entityType, world);
    }

    public CustomAreaEffectCloudEntity(World world, double x, double y, double z) {
        this(ModEntities.CUSTOM_AREA_EFFECT_CLOUD_ENTITY, world);
        this.setPosition(x, y, z);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(this.getRadius() * 2.0F, 3F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            this.spawnClientParticles();
        }
    }

    private void spawnClientParticles() {
        float densityFactor = getDensityFactor();

        if(densityFactor <= 0.0F) {
            return;
        }

        ParticleEffect particle = getParticleArea();
        float radius = this.getRadius();
        int count = MathHelper.ceil((2.0 / 3.0) * Math.PI * radius * radius * radius * densityFactor); // Ajuste para semiesfera

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

            this.getWorld().addImportantParticleClient(particle, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DENSITY_FACTOR, 0.0F);
        builder.add(PARTICLE_EFFECT, ParticleTypes.EFFECT);
    }

    public void setDensityFactor(float densityFactor) {
        this.dataTracker.set(DENSITY_FACTOR, densityFactor);
    }

    public float getDensityFactor() {
        return this.dataTracker.get(DENSITY_FACTOR);
    }

    public void setParticleArea(ParticleEffect particle) {
        this.dataTracker.set(PARTICLE_EFFECT, particle);
    }

    public ParticleEffect getParticleArea() {
        return this.dataTracker.get(PARTICLE_EFFECT);
    }


    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putFloat("DensityFactor", getDensityFactor());
        view.put("CustomParticle", ParticleTypes.TYPE_CODEC, getParticleArea());
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        setDensityFactor(view.getFloat("DensityFactor", 0.0F));
        view.read("CustomParticle", ParticleTypes.TYPE_CODEC)
                .ifPresent(this::setParticleArea);
    }
}
