package com.cassie77.bloater;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.AnimationState;


@Environment(EnvType.CLIENT)
public class BloaterRenderState extends LivingEntityRenderState{
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState roaringAnimationState = new AnimationState();
    public final AnimationState throwingAnimationState = new AnimationState();

    public ItemRenderState rightHandItemState = new ItemRenderState();
    public ItemRenderState leftHandItemState = new ItemRenderState();

    public BloaterRenderState() {

    }

}
