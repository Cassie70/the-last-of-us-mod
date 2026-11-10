package com.cassie77.the_last_block_of_us.client.shambler;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * Animation definitions for the Shambler, adapted from the Blockbench export
 * to the vanilla 1.21.8 animation format.
 */
public class ShamblerAnimations {

    public static final AnimationDefinition GROAR = AnimationDefinition.Builder.withLength(1.5F).looping()
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4167F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.125F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("cabeza_completa",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, -10.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, -30.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -10.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.25F, KeyframeAnimations.degreeVec(0.0F, -30.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, -10.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("cabeza_completa",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("cabeza_completa",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_izquierdo",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-27.2989F, -3.4554F, -6.6606F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_izquierdo",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_izquierdo",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_izquierda",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_izquierda",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_izquierda",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_derecha",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4167F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_derecha",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_derecha",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_derecho",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_derecho",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_derecho",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .build();

    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(3.0F).looping()
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(2.0F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(2.9167F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("cabeza_completa",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_izquierdo",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -10.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5417F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(2.4167F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -10.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_derecho",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5417F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(2.4167F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .build();

    public static final AnimationDefinition ATTACK = AnimationDefinition.Builder.withLength(1.0F)
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.375F, KeyframeAnimations.degreeVec(35.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5417F, KeyframeAnimations.degreeVec(38.7319F, -24.5693F, -20.2732F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.625F, KeyframeAnimations.degreeVec(43.5229F, -33.6014F, -27.7259F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.8333F, KeyframeAnimations.degreeVec(48.5229F, -33.6014F, -27.7259F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F, KeyframeAnimations.scaleVec(1.2F, 1.2F, 1.2F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("cabeza_completa",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 15.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.8333F, KeyframeAnimations.degreeVec(-5.0F, 15.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 15.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_izquierdo",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4583F, KeyframeAnimations.degreeVec(-52.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F, KeyframeAnimations.degreeVec(-51.5365F, -11.8491F, -9.2644F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_derecho",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(-50.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_izquierda",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.2083F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_derecha",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.5F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .build();

    public static final AnimationDefinition DEATH = AnimationDefinition.Builder.withLength(1.0F)
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4167F, KeyframeAnimations.degreeVec(52.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -9.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0F, KeyframeAnimations.scaleVec(1.2F, 1.2F, 1.2F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("cabeza_completa",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.7917F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_izquierdo",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F, KeyframeAnimations.degreeVec(-54.5868F, -8.1778F, -5.7751F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_derecho",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F, KeyframeAnimations.degreeVec(-69.7619F, 5.9438F, 4.5822F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_izquierda",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(87.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_izquierda",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -10.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_derecha",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.degreeVec(87.4118F, -14.9854F, 0.6696F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_derecha",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -10.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .build();

    public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(2.0F).looping()
            .addAnimation("torso",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.3333F, KeyframeAnimations.degreeVec(17.8487F, 10.5781F, 3.3808F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.75F, KeyframeAnimations.degreeVec(19.0654F, -21.2296F, -7.1224F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.6667F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_izquierdo",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, -12.5F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.5F, KeyframeAnimations.degreeVec(-37.5F, 0.0F, -12.5F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5F, KeyframeAnimations.degreeVec(-20.9359F, -3.2644F, -17.4008F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(2.0F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, -12.5F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_izquierda",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.7917F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.2083F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5833F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("pierna_derecha",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.4167F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.7917F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.2083F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.5833F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("brazo_derecho",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-10.6722F, 8.7864F, 21.2261F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.0417F, KeyframeAnimations.degreeVec(-28.1722F, 8.7864F, 21.2261F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(1.9583F, KeyframeAnimations.degreeVec(-10.6722F, 8.7864F, 21.2261F),
                                    AnimationChannel.Interpolations.LINEAR)))
            .build();
}
