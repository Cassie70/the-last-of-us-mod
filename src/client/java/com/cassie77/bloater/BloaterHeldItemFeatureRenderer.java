package com.cassie77.bloater;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;

public class BloaterHeldItemFeatureRenderer extends FeatureRenderer<BloaterRenderState, BloaterModel> {
    private final ItemRenderer itemRenderer;

    public BloaterHeldItemFeatureRenderer(FeatureRendererContext<BloaterRenderState, BloaterModel> context) {
        super(context);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, BloaterRenderState state, float limbAngle, float limbDistance) {

        if (state.heldItem.isEmpty()) return;

        matrices.push();

        BloaterModel model = this.getContextModel();
        model.setArmAngle(Arm.LEFT, matrices);

        matrices.translate(0, (11 * 0.0625) - 0.2, 0); // 0.0625 = factor de píxel a bloque
        matrices.scale(0.75f, 0.75f, 0.75f);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));

        itemRenderer.renderItem(
                null, // No hay entidad
                state.heldItem,
                ItemDisplayContext.THIRD_PERSON_LEFT_HAND, // Nuevo enum
                matrices,
                vertexConsumers,
                null, // world
                light,
                OverlayTexture.DEFAULT_UV,
                0 // seed
        );

        matrices.pop();
    }


}
