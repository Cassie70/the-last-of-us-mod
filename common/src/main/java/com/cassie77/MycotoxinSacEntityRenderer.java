package com.cassie77;

import com.cassie77.item.micotoxinsac.MycotoxinSacEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class MycotoxinSacEntityRenderer extends ThrownItemRenderer<MycotoxinSacEntity> {
    public MycotoxinSacEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
