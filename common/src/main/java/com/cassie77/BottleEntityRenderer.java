package com.cassie77;

import com.cassie77.item.bottle.BottleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class BottleEntityRenderer extends ThrownItemRenderer<BottleEntity> {
    public BottleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
