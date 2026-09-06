package com.cassie77.the_last_block_of_us.clicker;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class ClickerRenderState extends LivingEntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState roaringAnimationState = new AnimationState();

    public ClickerRenderState() {
    }
}
