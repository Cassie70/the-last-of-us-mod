package com.cassie77.the_last_block_of_us.client;

import com.cassie77.the_last_block_of_us.item.bottle.BottleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class BottleEntityRenderer extends ThrownItemRenderer<BottleEntity> {
    public BottleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
