package com.cassie77.clicker;

import com.cassie77.Constants;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class ClickerModel extends EntityModel<ClickerRenderState> {

    public static final ModelLayerLocation CLICKER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "clicker"), "main");

    private final ModelPart torso;
    private final ModelPart cabeza_completa;
    private final ModelPart brazo_izquierdo;
    private final ModelPart brazo_derecho;
    private final ModelPart pierna_izquierda;
    private final ModelPart pierna_derecha;

    private final KeyframeAnimation attackingAnimation;
    private final KeyframeAnimation roaringAnimation;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation walkingAnimation;

    public ClickerModel(ModelPart root) {
        super(root);
        this.torso = root.getChild("torso");
        this.cabeza_completa = this.torso.getChild("cabeza_completa");
        this.brazo_izquierdo = this.torso.getChild("brazo_izquierdo");
        this.brazo_derecho = this.torso.getChild("brazo_derecho");
        this.pierna_izquierda = root.getChild("pierna_izquierda");
        this.pierna_derecha = root.getChild("pierna_derecha");


        this.attackingAnimation = ClickerAnimations.ATTACK.bake(root);
        this.roaringAnimation = ClickerAnimations.GROAR.bake(root);
        this.idleAnimation = ClickerAnimations.IDLE.bake(root);
        this.walkingAnimation = ClickerAnimations.WALK.bake(root);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition torso = modelPartData.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -11.0F, -2.0F, 8.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 2.0F));

        torso.addOrReplaceChild("cabeza_completa", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(40, 15).addBox(-5.0F, -8.0F, -4.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 23).addBox(4.0F, -5.0F, -4.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(40, 43).addBox(-4.0F, -7.0F, -5.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 43).addBox(1.0F, -8.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 43).addBox(2.0F, -4.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 32).addBox(-2.0F, -9.0F, -4.0F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(46, 46).addBox(-2.0F, -6.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 46).addBox(1.0F, -4.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 30).addBox(2.0F, -5.0F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 43).addBox(4.0F, -9.0F, -4.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 38).addBox(0.0F, -10.0F, -4.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 0.0F));

        torso.addOrReplaceChild("brazo_izquierdo", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -9.0F, 0.0F));
        torso.addOrReplaceChild("brazo_derecho", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -9.0F, 0.0F));

        modelPartData.addOrReplaceChild("pierna_izquierda", CubeListBuilder.create().texOffs(24, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 12.0F, 2.0F));
        modelPartData.addOrReplaceChild("pierna_derecha", CubeListBuilder.create().texOffs(0, 31).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 2.0F));

        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void setupAnim(ClickerRenderState state) {
        super.setupAnim(state);
        this.setHeadAngle(state.yRot, state.xRot);

        this.walkingAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2.0F, 2.5F);
        this.attackingAnimation.apply(state.attackingAnimationState, state.ageInTicks);
        this.roaringAnimation.apply(state.roaringAnimationState, state.ageInTicks);
        this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks);
    }

    private void setHeadAngle(float yaw, float pitch) {
        this.cabeza_completa.xRot = pitch * ((float) Math.PI / 180F);
        this.cabeza_completa.yRot = yaw * ((float) Math.PI / 180F);
    }
}
