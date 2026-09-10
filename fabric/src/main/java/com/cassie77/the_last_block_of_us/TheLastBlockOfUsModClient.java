package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.client.*;
import com.cassie77.the_last_block_of_us.client.bloater.BloaterRenderer;
import com.cassie77.the_last_block_of_us.client.clicker.ClickerModel;
import com.cassie77.the_last_block_of_us.client.bloater.BloaterModel;
import com.cassie77.the_last_block_of_us.client.clicker.ClickerRenderer;
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
        EntityRendererRegistry.register(ModEntities.CLICKER, ClickerRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(BloaterModel.BLOATER, BloaterModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.BLOATER, BloaterRenderer::new);
    }
}
