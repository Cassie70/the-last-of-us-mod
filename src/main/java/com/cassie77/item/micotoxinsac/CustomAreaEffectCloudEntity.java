package com.cassie77.item.micotoxinsac;

import com.cassie77.ModEntities;
import net.minecraft.entity.*;
import net.minecraft.world.World;

public class CustomAreaEffectCloudEntity extends AreaEffectCloudEntity {


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
}
