package com.cassie77.the_last_block_of_us.bloater;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemStack;

public class BloaterRenderState extends LivingEntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState roaringAnimationState = new AnimationState();
    public final AnimationState throwingAnimationState = new AnimationState();
    public ItemStack heldItem = ItemStack.EMPTY;

    public BloaterRenderState() {
    }
}
