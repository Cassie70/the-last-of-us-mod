package com.cassie77.the_last_block_of_us.client;

import com.cassie77.the_last_block_of_us.item.nailbomb.NailBombEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class NailBombEntityRenderer extends ThrownItemRenderer<NailBombEntity> {
    public NailBombEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
