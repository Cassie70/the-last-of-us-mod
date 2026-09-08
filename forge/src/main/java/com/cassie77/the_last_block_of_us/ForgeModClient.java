package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.bloater.BloaterModel;
import com.cassie77.the_last_block_of_us.bloater.BloaterRenderer;
import com.cassie77.the_last_block_of_us.clicker.ClickerModel;
import com.cassie77.the_last_block_of_us.clicker.ClickerRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;

public final class ForgeModClient {

    private ForgeModClient() {
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ClickerModel.CLICKER, ClickerModel::getTexturedModelData);
        event.registerLayerDefinition(BloaterModel.BLOATER, BloaterModel::getTexturedModelData);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MOLOTOV_ENTITY, MolotovEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.BOTTLE_ENTITY, BottleEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.NAIL_BOMB_ENTITY, NailBombEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.MYCOTOXIN_SAC_ENTITY, MycotoxinSacEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.CUSTOM_AREA_EFFECT_CLOUD_ENTITY, NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.SMOKE_BOMB_ENTITY, SmokeBombEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.CLICKER, ClickerRenderer::new);
        event.registerEntityRenderer(ModEntities.BLOATER, BloaterRenderer::new);
    }
}
