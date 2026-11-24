package com.cassie77.the_last_block_of_us.client.bloater;

import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
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
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BloaterHeldItemFeatureRenderer extends RenderLayer<BloaterEntity, BloaterModel> {
    private final ItemRenderer itemRenderer;

    public BloaterHeldItemFeatureRenderer(RenderLayerParent<BloaterEntity, BloaterModel> context) {
        super(context);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(@NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light,
                       @NotNull BloaterEntity entity, float limbAngle, float limbDistance, float partialTick,
                       float animationProgress, float headYaw, float headPitch) {
        ItemStack heldItem = entity.getOffhandItem();
        if (heldItem.isEmpty()) return;

        matrices.pushPose();

        BloaterModel model = this.getParentModel();
        model.translateToHand(HumanoidArm.LEFT, matrices);

        matrices.translate(0, (11 * 0.0625) - 0.2, 0);
        matrices.scale(0.75f, 0.75f, 0.75f);

        matrices.mulPose(Axis.XP.rotationDegrees(-90));

        itemRenderer.renderStatic(
                heldItem,
                ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                light,
                OverlayTexture.NO_OVERLAY,
                matrices,
                vertexConsumers,
                entity.level(),
                entity.getId()
        );

        matrices.popPose();
    }
}
