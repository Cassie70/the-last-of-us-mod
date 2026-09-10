package com.cassie77.the_last_block_of_us.client.clicker;

import com.cassie77.the_last_block_of_us.Constants;
import com.cassie77.the_last_block_of_us.entity.clicker.ClickerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ClickerRenderer extends MobRenderer<ClickerEntity, ClickerRenderState, ClickerModel> {

    public ClickerRenderer(EntityRendererProvider.Context context) {
        super(context, new ClickerModel(context.bakeLayer(ClickerModel.CLICKER)), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ClickerRenderState state) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/clicker/clicker.png");
    }

    @Override
    public @NotNull ClickerRenderState createRenderState() {
        return new ClickerRenderState();
    }

    @Override
    public void extractRenderState(@NotNull ClickerEntity clicker, @NotNull ClickerRenderState clickerRenderState, float f) {
        super.extractRenderState(clicker, clickerRenderState, f);

        clickerRenderState.roaringAnimationState.copyFrom(clicker.roaringAnimationState);
        clickerRenderState.idleAnimationState.copyFrom(clicker.idleAnimationState);
        clickerRenderState.attackingAnimationState.copyFrom(clicker.attackingAnimationState);
    }
}
