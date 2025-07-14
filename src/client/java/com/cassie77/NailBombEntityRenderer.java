package com.cassie77;

import com.cassie77.nailbomb.NailBombEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class NailBombEntityRenderer extends FlyingItemEntityRenderer<NailBombEntity> {
    public NailBombEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
}
