package com.cassie77.the_last_block_of_us.client.bloater;

import com.cassie77.the_last_block_of_us.Constants;
import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class BloaterModel extends HierarchicalModel<BloaterEntity> {
    private final ModelPart root;


    public static final ModelLayerLocation BLOATER = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "bloater"), "main");

    private final ModelPart torso;
    private final ModelPart torso2;
    private final ModelPart cabeza_completa;
    private final ModelPart brazo_izquierdo;
    private final ModelPart brazo_derecho;
    private final ModelPart pierna_izquierda;
    private final ModelPart pierna_derecha;

    private final AnimationDefinition attackingAnimation;
    private final AnimationDefinition roaringAnimation;
    private final AnimationDefinition idleAnimation;
    private final AnimationDefinition walkingAnimation;
    private final AnimationDefinition throwingAnimation;

    public BloaterModel(ModelPart root) {
        super();
        this.root = root;
        this.torso = root.getChild("torso");
        this.torso2 = this.torso.getChild("torso2");
        this.cabeza_completa = this.torso2.getChild("cabeza_completa");
        this.brazo_izquierdo = this.torso2.getChild("brazo_izquierdo");
        this.brazo_derecho = this.torso2.getChild("brazo_derecho");
        this.pierna_izquierda = this.torso.getChild("pierna_izquierda");
        this.pierna_derecha = this.torso.getChild("pierna_derecha");

        this.attackingAnimation = BloaterAnimations.ATTACK;
        this.roaringAnimation = BloaterAnimations.ROAR;
        this.idleAnimation = BloaterAnimations.IDLE;
        this.walkingAnimation = BloaterAnimations.WALK;
        this.throwingAnimation = BloaterAnimations.THROW;
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition torso = modelPartData.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 2.0F));

        PartDefinition torso2 = torso.addOrReplaceChild("torso2", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -11.0F, -2.0F, 8.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(20, 51).addBox(-4.0F, -8.0F, -3.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 26).addBox(-4.0F, -4.0F, -3.0F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 29).addBox(1.0F, -2.0F, -3.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 29).addBox(1.0F, -6.0F, -3.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 29).addBox(-4.0F, -11.0F, -3.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 52).addBox(-1.0F, -8.0F, -3.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 52).addBox(1.0F, -10.0F, -3.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 55).addBox(-2.0F, -2.0F, -3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 29).addBox(0.0F, -8.0F, 2.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-4.0F, -3.0F, 2.0F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 29).addBox(-4.0F, -11.0F, 2.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 54).addBox(-4.0F, -7.0F, 2.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        torso2.addOrReplaceChild("cabeza_completa", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 39).addBox(-5.0F, -10.0F, -5.0F, 1.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 47).addBox(-6.0F, -10.0F, -5.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 15).addBox(4.0F, -9.0F, -5.0F, 1.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(10, 47).addBox(5.0F, -10.0F, -5.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(52, 12).addBox(-4.0F, -7.0F, -5.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 33).addBox(1.0F, -8.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 44).addBox(-4.0F, -9.0F, -3.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 32).addBox(1.0F, -9.0F, -5.0F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(20, 47).addBox(2.0F, -10.0F, -5.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 0.0F));

        torso2.addOrReplaceChild("brazo_izquierdo", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 48).addBox(-1.0F, -3.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(52, 22).addBox(-2.0F, 2.0F, -3.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(52, 22).addBox(-2.0F, -2.0F, -3.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(52, 16).addBox(-2.0F, -2.0F, 2.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(52, 52).addBox(-2.0F, 5.0F, 2.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 52).addBox(2.0F, 6.0F, -2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(32, 50).addBox(2.0F, 0.0F, -2.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -9.0F, 0.0F));

        torso2.addOrReplaceChild("brazo_derecho", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(52, 22).addBox(-2.0F, 6.0F, -3.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 4).addBox(-2.0F, -2.0F, -3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 39).addBox(-2.0F, -3.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(52, 16).addBox(-2.0F, -2.0F, 2.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 52).addBox(-3.0F, 6.0F, -2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 4).addBox(-3.0F, 0.0F, -2.0F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -9.0F, 0.0F));

        torso.addOrReplaceChild("pierna_izquierda", CubeListBuilder.create().texOffs(24, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));
        torso.addOrReplaceChild("pierna_derecha", CubeListBuilder.create().texOffs(0, 31).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(@NotNull BloaterEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.setHeadAngle(netHeadYaw, headPitch);

        this.animate(entity.idleAnimationState, this.idleAnimation, ageInTicks);
        this.animateWalk(this.walkingAnimation, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        this.animate(entity.roaringAnimationState, this.roaringAnimation, ageInTicks);
        this.animate(entity.throwingAnimationState, this.throwingAnimation, ageInTicks);
        this.animate(entity.attackingAnimationState, this.attackingAnimation, ageInTicks);
    }

    private void setHeadAngle(float yaw, float pitch) {
        this.cabeza_completa.xRot = pitch * ((float) Math.PI / 180F);
        this.cabeza_completa.yRot = yaw * ((float) Math.PI / 180F);
    }

    public void translateToHand(HumanoidArm arm, PoseStack matrices) {
        torso.translateAndRotate(matrices);
        torso2.translateAndRotate(matrices);
        if (arm == HumanoidArm.LEFT) {
            brazo_izquierdo.translateAndRotate(matrices);
        } else {
            brazo_derecho.translateAndRotate(matrices);
        }
    }
}
