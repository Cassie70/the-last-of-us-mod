package com.cassie77;

import com.cassie77.item.nailbomb.NailBombEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class NailBombEntityRenderer extends ThrownItemRenderer<NailBombEntity> {
    public NailBombEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
