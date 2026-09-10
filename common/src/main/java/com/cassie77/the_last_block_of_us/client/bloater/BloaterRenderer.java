package com.cassie77.the_last_block_of_us.client.bloater;

import com.cassie77.the_last_block_of_us.Constants;
import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BloaterRenderer extends MobRenderer<BloaterEntity, BloaterRenderState, BloaterModel> {

    public BloaterRenderer(EntityRendererProvider.Context context) {
        super(context, new BloaterModel(context.bakeLayer(BloaterModel.BLOATER)), 0.5f);
        this.addLayer(new BloaterHeldItemFeatureRenderer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BloaterRenderState state) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/bloater/bloater.png");
    }

    @Override
    protected void scale(@NotNull BloaterRenderState state, PoseStack matrices) {
        float scale = 1.25f;
        matrices.scale(scale, scale, scale);
        super.scale(state, matrices);
    }

    @Override
    public @NotNull BloaterRenderState createRenderState() {
        return new BloaterRenderState();
    }

    @Override
    public void extractRenderState(@NotNull BloaterEntity bloater, @NotNull BloaterRenderState bloaterRenderState, float f) {
        super.extractRenderState(bloater, bloaterRenderState, f);

        bloaterRenderState.roaringAnimationState.copyFrom(bloater.roaringAnimationState);
        bloaterRenderState.idleAnimationState.copyFrom(bloater.idleAnimationState);
        bloaterRenderState.attackingAnimationState.copyFrom(bloater.attackingAnimationState);
        bloaterRenderState.throwingAnimationState.copyFrom(bloater.throwingAnimationState);
        bloaterRenderState.heldItem = bloater.getOffhandItem();
    }
}
