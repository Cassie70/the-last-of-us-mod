package com.cassie77.bloater;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;

public class BloaterHeldItemFeatureRenderer extends FeatureRenderer<BloaterRenderState, BloaterModel> {

    public BloaterHeldItemFeatureRenderer(FeatureRendererContext<BloaterRenderState, BloaterModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, BloaterRenderState state, float limbSwing, float limbSwingAmount) {
        this.renderItem(state, state.rightHandItemState, Arm.RIGHT, matrices, vertexConsumers, light);
        this.renderItem(state, state.leftHandItemState, Arm.LEFT, matrices, vertexConsumers, light);
    }

    protected void renderItem(BloaterRenderState state, ItemRenderState itemState, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!itemState.isEmpty()) {
            matrices.push();
            this.getContextModel().setArmAngle(arm, matrices);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            boolean left = arm == Arm.LEFT;
            matrices.translate((left ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            itemState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }
}
