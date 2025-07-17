package com.cassie77;

import com.cassie77.clicker.ClickerModel;
import com.cassie77.clicker.ClickerRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class TheLastOfUsModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRendererRegistry.register(ModEntities.MOLOTOV_ENTITY, MolotovEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.BOTTLE_ENTITY, BottleEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.NAIL_BOMB_ENTITY, NailBombEntityRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(ClickerModel.CLICKER, ClickerModel::getTexturedModelData);
		EntityRendererRegistry.register(ModEntities.CLICKER_ENTITY, ClickerRenderer::new);


	}
}