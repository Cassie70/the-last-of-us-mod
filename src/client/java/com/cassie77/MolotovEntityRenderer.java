package com.cassie77;

import com.cassie77.molotov.MolotovEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class MolotovEntityRenderer extends FlyingItemEntityRenderer<MolotovEntity> {
    public MolotovEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
}