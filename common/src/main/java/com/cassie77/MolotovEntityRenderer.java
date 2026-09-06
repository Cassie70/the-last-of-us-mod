package com.cassie77;

import com.cassie77.item.molotov.MolotovEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class MolotovEntityRenderer extends ThrownItemRenderer<MolotovEntity> {
    public MolotovEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
