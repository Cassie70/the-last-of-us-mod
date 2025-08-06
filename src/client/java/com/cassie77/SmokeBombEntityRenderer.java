package com.cassie77;

import com.cassie77.item.smokebomb.SmokeBombEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class SmokeBombEntityRenderer extends FlyingItemEntityRenderer<SmokeBombEntity> {
    public SmokeBombEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
}
