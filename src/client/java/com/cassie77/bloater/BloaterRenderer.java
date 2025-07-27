package com.cassie77.bloater;

import com.cassie77.ModItems;
import com.cassie77.TheLastOfUsMod;
import com.cassie77.entity.bloater.BloaterEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class BloaterRenderer extends MobEntityRenderer<BloaterEntity, BloaterRenderState, BloaterModel>{

    public BloaterRenderer(EntityRendererFactory.Context context) {
        super(context, new BloaterModel(context.getPart(BloaterModel.BLOATER)), 0.5f);

        this.addFeature(new BloaterHeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(BloaterRenderState state) {
        return Identifier.of(TheLastOfUsMod.MOD_ID, "textures/entity/bloater/bloater.png");
    }

    @Override
    protected void scale(BloaterRenderState state, MatrixStack matrices) {
        float scale = 1.25f;
        matrices.scale(scale, scale, scale);
        super.scale(state, matrices);
    }

    @Override
    public BloaterRenderState createRenderState() {
        return new BloaterRenderState();
    }

    public void updateRenderState(BloaterEntity bloater, BloaterRenderState bloaterRenderState, float f) {
        super.updateRenderState(bloater, bloaterRenderState, f);

        bloaterRenderState.roaringAnimationState.copyFrom(bloater.roaringAnimationState);
        bloaterRenderState.idleAnimationState.copyFrom(bloater.idleAnimationState);
        bloaterRenderState.attackingAnimationState.copyFrom(bloater.attackingAnimationState);
        bloaterRenderState.throwingAnimationState.copyFrom(bloater.throwingAnimationState);
        bloaterRenderState.heldItem = bloater.getOffHandStack();

    }
}
