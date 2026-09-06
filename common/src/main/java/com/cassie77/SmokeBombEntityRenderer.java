package com.cassie77;

import com.cassie77.item.smokebomb.SmokeBombEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class SmokeBombEntityRenderer extends ThrownItemRenderer<SmokeBombEntity> {
    public SmokeBombEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
