package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.client.*;
import com.cassie77.the_last_block_of_us.client.bloater.BloaterModel;
import com.cassie77.the_last_block_of_us.client.bloater.BloaterRenderer;
import com.cassie77.the_last_block_of_us.client.clicker.ClickerModel;
import com.cassie77.the_last_block_of_us.client.clicker.ClickerRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class NeoForgeModClient {
    private NeoForgeModClient() {
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