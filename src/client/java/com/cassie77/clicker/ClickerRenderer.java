package com.cassie77.clicker;

import com.cassie77.TheLastOfUsMod;
import com.cassie77.entity.clicker.ClickerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class ClickerRenderer extends MobEntityRenderer<ClickerEntity, ClickerRenderState, ClickerModel> {


    public ClickerRenderer(EntityRendererFactory.Context context) {
        super(context, new ClickerModel(context.getPart(ClickerModel.CLICKER)), 0.5f);
    }

    @Override
    public Identifier getTexture(ClickerRenderState state) {
        return Identifier.of(TheLastOfUsMod.MOD_ID, "textures/entity/clicker/clicker.png");
    }

    @Override
    public ClickerRenderState createRenderState() {
        return new ClickerRenderState();
    }

    public void updateRenderState(ClickerEntity clicker, ClickerRenderState clickerRenderState, float f) {
        super.updateRenderState(clicker, clickerRenderState, f);

        clickerRenderState.roaringAnimationState.copyFrom(clicker.roaringAnimationState);
        clickerRenderState.idleAnimationState.copyFrom(clicker.idleAnimationState);
        clickerRenderState.attackingAnimationState.copyFrom(clicker.attackingAnimationState);
    }
}
