package com.cassie77.clicker;

import com.cassie77.TheLastOfUsMod;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ClickerModel extends EntityModel<ClickerRenderState> {

    public static final EntityModelLayer CLICKER = new EntityModelLayer(Identifier.of(TheLastOfUsMod.MOD_ID, "clicker"), "main");

    private final ModelPart torso;
    private final ModelPart cabeza_completa;
    private final ModelPart brazo_izquierdo;
    private final ModelPart brazo_derecho;
    private final ModelPart pierna_izquierda;
    private final ModelPart pierna_derecha;

    private final Animation attackingAnimation;
    private final Animation roaringAnimation;
    private final Animation idleAnimation;
    private final Animation walkingAnimation;

    public ClickerModel(ModelPart root) {
        super(root);
        this.torso = root.getChild("torso");
        this.cabeza_completa = this.torso.getChild("cabeza_completa");
        this.brazo_izquierdo = this.torso.getChild("brazo_izquierdo");
        this.brazo_derecho = this.torso.getChild("brazo_derecho");
        this.pierna_izquierda = root.getChild("pierna_izquierda");
        this.pierna_derecha = root.getChild("pierna_derecha");

        this.attackingAnimation = ClickerAnimations.ATTACK.createAnimation(root);
        this.roaringAnimation = ClickerAnimations.GROAR.createAnimation(root);
        this.idleAnimation = ClickerAnimations.IDLE.createAnimation(root);
        this.walkingAnimation = ClickerAnimations.WALK.createAnimation(root);
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData torso = modelPartData.addChild("torso", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -11.0F, -2.0F, 8.0F, 11.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 12.0F, 2.0F));

        ModelPartData cabeza_completa = torso.addChild("cabeza_completa", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(40, 15).cuboid(-5.0F, -8.0F, -4.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(40, 23).cuboid(4.0F, -5.0F, -4.0F, 1.0F, 4.0F, 3.0F, new Dilation(0.0F))
                .uv(40, 43).cuboid(-4.0F, -7.0F, -5.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(46, 43).cuboid(1.0F, -8.0F, -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(46, 43).cuboid(2.0F, -4.0F, -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 32).cuboid(-2.0F, -9.0F, -4.0F, 6.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(46, 46).cuboid(-2.0F, -6.0F, -5.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(46, 46).cuboid(1.0F, -4.0F, -5.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 30).cuboid(2.0F, -5.0F, -5.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 43).cuboid(4.0F, -9.0F, -4.0F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(32, 38).cuboid(0.0F, -10.0F, -4.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -11.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData brazo_izquierdo = torso.addChild("brazo_izquierdo", ModelPartBuilder.create().uv(32, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(6.0F, -9.0F, 0.0F));

        ModelPartData brazo_derecho = torso.addChild("brazo_derecho", ModelPartBuilder.create().uv(16, 32).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(-6.0F, -9.0F, 0.0F));

        ModelPartData pierna_izquierda = modelPartData.addChild("pierna_izquierda", ModelPartBuilder.create().uv(0, 31).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(2.0F, 12.0F, 2.0F));

        ModelPartData pierna_derecha = modelPartData.addChild("pierna_derecha", ModelPartBuilder.create().uv(24, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(-2.0F, 12.0F, 2.0F));
        return TexturedModelData.of(modelData, 64, 64);

    }
    @Override
    public void setAngles(ClickerRenderState state) {
        super.setAngles(state);
        this.setHeadAngle(state.relativeHeadYaw, state.pitch);

        this.walkingAnimation.applyWalking(state.limbSwingAnimationProgress, state.limbSwingAmplitude, 2.0F, 2.5F);
        this.attackingAnimation.apply(state.attackingAnimationState, state.age);
        this.roaringAnimation.apply(state.roaringAnimationState, state.age);
        this.idleAnimation.apply(state.idleAnimationState, state.age);

    }

    private void setHeadAngle(float yaw, float pitch) {
        this.cabeza_completa.pitch = pitch * ((float)Math.PI / 180F);
        this.cabeza_completa.yaw = yaw * ((float)Math.PI / 180F);
    }

}
