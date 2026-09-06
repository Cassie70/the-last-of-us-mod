package com.cassie77.clicker;

import com.cassie77.Constants;
import com.cassie77.entity.clicker.ClickerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ClickerRenderer extends MobRenderer<ClickerEntity, ClickerRenderState, ClickerModel> {

    public ClickerRenderer(EntityRendererProvider.Context context) {
        super(context, new ClickerModel(context.bakeLayer(ClickerModel.CLICKER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ClickerRenderState state) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/clicker/clicker.png");
    }

    @Override
    public ClickerRenderState createRenderState() {
        return new ClickerRenderState();
    }

    @Override
    public void extractRenderState(ClickerEntity clicker, ClickerRenderState clickerRenderState, float f) {
        super.extractRenderState(clicker, clickerRenderState, f);

        clickerRenderState.roaringAnimationState.copyFrom(clicker.roaringAnimationState);
        clickerRenderState.idleAnimationState.copyFrom(clicker.idleAnimationState);
        clickerRenderState.attackingAnimationState.copyFrom(clicker.attackingAnimationState);
    }
}
