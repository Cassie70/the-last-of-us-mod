package com.cassie77.the_last_block_of_us.client.bloater;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class BloaterHeldItemFeatureRenderer extends RenderLayer<BloaterRenderState, BloaterModel> {
    private final ItemRenderer itemRenderer;

    public BloaterHeldItemFeatureRenderer(RenderLayerParent<BloaterRenderState, BloaterModel> context) {
        super(context);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(@NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light, BloaterRenderState state, float limbAngle, float limbDistance) {
        if (state.heldItem.isEmpty()) return;

        matrices.pushPose();

        BloaterModel model = this.getParentModel();
        model.translateToHand(HumanoidArm.LEFT, matrices);

        matrices.translate(0, (11 * 0.0625) - 0.2, 0);
        matrices.scale(0.75f, 0.75f, 0.75f);

        matrices.mulPose(Axis.XP.rotationDegrees(-90));

        itemRenderer.renderStatic(
                state.heldItem,
                ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                light,
                OverlayTexture.NO_OVERLAY,
                matrices,
                vertexConsumers,
                null,
                0
        );

        matrices.popPose();
    }
}
