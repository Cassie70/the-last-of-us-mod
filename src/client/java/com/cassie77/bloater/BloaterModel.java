package com.cassie77.bloater;

import com.cassie77.TheLastOfUsMod;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import java.util.logging.Logger;

public class BloaterModel extends EntityModel<BloaterRenderState> {

    public static final EntityModelLayer BLOATER = new EntityModelLayer(Identifier.of(TheLastOfUsMod.MOD_ID, "bloater"), "main");

    private final ModelPart torso;
    private final ModelPart torso2;
    private final ModelPart cabeza_completa;
    private final ModelPart brazo_izquierdo;
    private final ModelPart brazo_derecho;
    private final ModelPart pierna_izquierda;
    private final ModelPart pierna_derecha;

    private final Animation attackingAnimation;
    private final Animation roaringAnimation;
    private final Animation idleAnimation;
    private final Animation walkingAnimation;
    private final Animation throwingAnimation;

    public BloaterModel(ModelPart root) {
        super(root);
        
        this.torso = root.getChild("torso");
        this.torso2 = this.torso.getChild("torso2");
        this.cabeza_completa = this.torso2.getChild("cabeza_completa");
        this.brazo_izquierdo = this.torso2.getChild("brazo_izquierdo");
        this.brazo_derecho = this.torso2.getChild("brazo_derecho");
        this.pierna_izquierda = this.torso.getChild("pierna_izquierda");
        this.pierna_derecha = this.torso.getChild("pierna_derecha");

        this.attackingAnimation = BloaterAnimations.ATTACK.createAnimation(root);
        this.roaringAnimation = BloaterAnimations.ROAR.createAnimation(root);
        this.idleAnimation = BloaterAnimations.IDLE.createAnimation(root);
        this.walkingAnimation = BloaterAnimations.WALK.createAnimation(root);
        this.throwingAnimation = BloaterAnimations.THROW.createAnimation(root);
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData torso = modelPartData.addChild("torso", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 12.0F, 2.0F));

        ModelPartData torso2 = torso.addChild("torso2", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -11.0F, -2.0F, 8.0F, 11.0F, 4.0F, new Dilation(0.0F))
                .uv(20, 51).cuboid(-4.0F, -8.0F, -3.0F, 3.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 26).cuboid(-4.0F, -4.0F, -3.0F, 7.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 29).cuboid(1.0F, -2.0F, -3.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 29).cuboid(1.0F, -6.0F, -3.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 29).cuboid(-4.0F, -11.0F, -3.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 52).cuboid(-1.0F, -8.0F, -3.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 52).cuboid(1.0F, -10.0F, -3.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 55).cuboid(-2.0F, -2.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(50, 29).cuboid(0.0F, -8.0F, 2.0F, 4.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(48, 0).cuboid(-4.0F, -3.0F, 2.0F, 5.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(50, 29).cuboid(-4.0F, -11.0F, 2.0F, 4.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(10, 54).cuboid(-4.0F, -7.0F, 2.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData cabeza_completa = torso2.addChild("cabeza_completa", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(32, 39).cuboid(-5.0F, -10.0F, -5.0F, 1.0F, 6.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 47).cuboid(-6.0F, -10.0F, -5.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(40, 15).cuboid(4.0F, -9.0F, -5.0F, 1.0F, 6.0F, 5.0F, new Dilation(0.0F))
                .uv(10, 47).cuboid(5.0F, -10.0F, -5.0F, 1.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(52, 12).cuboid(-4.0F, -7.0F, -5.0F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(50, 33).cuboid(1.0F, -8.0F, -5.0F, 3.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 44).cuboid(-4.0F, -9.0F, -3.0F, 4.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(32, 32).cuboid(1.0F, -9.0F, -5.0F, 3.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(20, 47).cuboid(2.0F, -10.0F, -5.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -11.0F, 0.0F));

        ModelPartData brazo_izquierdo = torso2.addChild("brazo_izquierdo", ModelPartBuilder.create().uv(32, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new Dilation(0.0F))
                .uv(44, 48).cuboid(-1.0F, -3.0F, -2.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(52, 22).cuboid(-2.0F, 2.0F, -3.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(52, 22).cuboid(-2.0F, -2.0F, -3.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(52, 16).cuboid(-2.0F, -2.0F, 2.0F, 2.0F, 5.0F, 1.0F, new Dilation(0.0F))
                .uv(52, 52).cuboid(-2.0F, 5.0F, 2.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(46, 52).cuboid(2.0F, 6.0F, -2.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(32, 50).cuboid(2.0F, 0.0F, -2.0F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(6.0F, -9.0F, 0.0F));

        ModelPartData brazo_derecho = torso2.addChild("brazo_derecho", ModelPartBuilder.create().uv(16, 32).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new Dilation(0.0F))
                .uv(52, 22).cuboid(-2.0F, 6.0F, -3.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(56, 4).cuboid(-2.0F, -2.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 39).cuboid(-2.0F, -3.0F, -2.0F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(52, 16).cuboid(-2.0F, -2.0F, 2.0F, 2.0F, 5.0F, 1.0F, new Dilation(0.0F))
                .uv(46, 52).cuboid(-3.0F, 6.0F, -2.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(48, 4).cuboid(-3.0F, 0.0F, -2.0F, 1.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(-6.0F, -9.0F, 0.0F));

        ModelPartData pierna_izquierda = torso.addChild("pierna_izquierda", ModelPartBuilder.create().uv(24, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(2.0F, 0.0F, 0.0F));

        ModelPartData pierna_derecha = torso.addChild("pierna_derecha", ModelPartBuilder.create().uv(0, 31).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(-2.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(BloaterRenderState state) {
        super.setAngles(state);
        this.setHeadAngle(state.relativeHeadYaw, state.pitch);

        this.walkingAnimation.applyWalking(state.limbSwingAnimationProgress, state.limbSwingAmplitude, 2.0F, 2.5F);
        this.attackingAnimation.apply(state.attackingAnimationState, state.age);
        this.roaringAnimation.apply(state.roaringAnimationState, state.age);
        this.idleAnimation.apply(state.idleAnimationState, state.age);
        this.throwingAnimation.apply(state.throwingAnimationState, state.age);

    }

    private void setHeadAngle(float yaw, float pitch) {
        this.cabeza_completa.pitch = pitch * ((float)Math.PI / 180F);
        this.cabeza_completa.yaw = yaw * ((float)Math.PI / 180F);
    }

}
