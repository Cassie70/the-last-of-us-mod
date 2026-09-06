package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.item.molotov.MolotovEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class MolotovEntityRenderer extends ThrownItemRenderer<MolotovEntity> {
    public MolotovEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
