package com.cassie77;

import com.cassie77.item.bottle.BottleEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class BottleEntityRenderer extends FlyingItemEntityRenderer<BottleEntity> {
    public BottleEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
}
