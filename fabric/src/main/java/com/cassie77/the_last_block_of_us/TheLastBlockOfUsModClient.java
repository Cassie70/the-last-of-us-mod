package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.clicker.ClickerModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;

public class TheLastBlockOfUsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.MOLOTOV_ENTITY, MolotovEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BOTTLE_ENTITY, BottleEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.NAIL_BOMB_ENTITY, NailBombEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MYCOTOXIN_SAC_ENTITY, MycotoxinSacEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.CUSTOM_AREA_EFFECT_CLOUD_ENTITY, NoopRenderer::new);
        EntityRendererRegistry.register(ModEntities.SMOKE_BOMB_ENTITY, SmokeBombEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ClickerModel.CLICKER, ClickerModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.CLICKER, com.cassie77.the_last_block_of_us.clicker.ClickerRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(com.cassie77.the_last_block_of_us.bloater.BloaterModel.BLOATER, com.cassie77.the_last_block_of_us.bloater.BloaterModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.BLOATER, com.cassie77.the_last_block_of_us.bloater.BloaterRenderer::new);
    }
}
