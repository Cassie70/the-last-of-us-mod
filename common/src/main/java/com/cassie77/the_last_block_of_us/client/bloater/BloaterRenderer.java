package com.cassie77.the_last_block_of_us.client.bloater;

import com.cassie77.the_last_block_of_us.Constants;
import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BloaterRenderer extends MobRenderer<BloaterEntity, BloaterModel> {

    public BloaterRenderer(EntityRendererProvider.Context context) {
        super(context, new BloaterModel(context.bakeLayer(BloaterModel.BLOATER)), 0.5f);
        this.addLayer(new BloaterHeldItemFeatureRenderer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BloaterEntity entity) {
        return new ResourceLocation(Constants.MOD_ID, "textures/entity/bloater/bloater.png");
    }

    @Override
    protected void scale(@NotNull BloaterEntity entity, @NotNull PoseStack matrices, float partialTick) {
        float scale = 1.25f;
        matrices.scale(scale, scale, scale);
        super.scale(entity, matrices, partialTick);
    }
}
