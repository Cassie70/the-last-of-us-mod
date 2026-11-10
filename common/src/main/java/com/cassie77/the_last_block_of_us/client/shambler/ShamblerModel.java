package com.cassie77.the_last_block_of_us.client.shambler;

import com.cassie77.the_last_block_of_us.Constants;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Model for the Shambler, adapted from the Blockbench export to the
 * vanilla 1.21.8 {@link EntityModel} render-state pattern.
 */
public class ShamblerModel extends EntityModel<ShamblerRenderState> {

    public static final ModelLayerLocation SHAMBLER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "shambler"), "main");

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
    private final KeyframeAnimation deathAnimation;

    public ShamblerModel(ModelPart root) {
        super(root);
        this.torso = root.getChild("torso");
        this.cabeza_completa = this.torso.getChild("cabeza_completa");
        this.brazo_izquierdo = this.torso.getChild("brazo_izquierdo");
        this.brazo_derecho = this.torso.getChild("brazo_derecho");
        this.pierna_izquierda = root.getChild("pierna_izquierda");
        this.pierna_derecha = root.getChild("pierna_derecha");

        this.attackingAnimation = ShamblerAnimations.ATTACK.bake(root);
        this.roaringAnimation = ShamblerAnimations.GROAR.bake(root);
        this.idleAnimation = ShamblerAnimations.IDLE.bake(root);
        this.walkingAnimation = ShamblerAnimations.WALK.bake(root);
        this.deathAnimation = ShamblerAnimations.DEATH.bake(root);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        PartDefinition torso = modelPartData.addOrReplaceChild("torso", CubeListBuilder.create()
                        .texOffs(48, 30).addBox(-4.0F, -11.0F, 2.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(58, 20).addBox(-4.0F, -9.0F, 2.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(26, 60).addBox(-4.0F, -7.0F, 2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(58, 24).addBox(-4.0F, -11.0F, -3.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 16).addBox(-4.0F, -11.0F, -2.0F, 8.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 0).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.0F, 2.0F));

        PartDefinition cabeza_completa = torso.addOrReplaceChild("cabeza_completa", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(26, 55).addBox(4.0F, -3.0F, -3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 50).addBox(-5.0F, -10.0F, -1.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(42, 60).addBox(5.0F, -7.0F, 1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 47).addBox(-5.0F, -8.0F, -3.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 48).addBox(-7.0F, -4.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(60, 38).addBox(-7.0F, -8.0F, 1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 55).addBox(-7.0F, -6.0F, -1.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 48).addBox(-6.0F, -6.0F, 3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(60, 34).addBox(3.0F, -5.0F, 4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(58, 16).addBox(-3.0F, -5.0F, 6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(34, 60).addBox(-3.0F, -3.0F, 6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 7).addBox(-4.0F, -7.0F, 4.0F, 7.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(26, 60).addBox(-2.0F, -9.0F, 4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(26, 60).addBox(-4.0F, -9.0F, 4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -11.0F, 0.0F, 0.0F, -0.0436F, 0.0F));

        cabeza_completa.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                        .texOffs(24, 16).addBox(0.0F, -8.0F, -1.0F, 1.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.0F, -9.0F, -3.0F, 0.0F, 0.0F, 1.5708F));

        cabeza_completa.addOrReplaceChild("cube_r2", CubeListBuilder.create()
                        .texOffs(42, 16).addBox(0.0F, -7.0F, -2.0F, 1.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.0F, -10.0F, -1.0F, 0.0F, 0.0F, 1.5708F));

        cabeza_completa.addOrReplaceChild("cube_r3", CubeListBuilder.create()
                        .texOffs(0, 48).addBox(0.0F, -7.0F, -1.0F, 1.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(5.0F, -3.0F, 3.0F, 0.0F, -3.098F, 0.0F));

        cabeza_completa.addOrReplaceChild("cube_r4", CubeListBuilder.create()
                        .texOffs(48, 34).addBox(0.0F, -4.0F, -1.0F, 1.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.0F, -5.0F, -4.0F, 0.0F, 1.5708F, 0.0F));

        torso.addOrReplaceChild("brazo_izquierdo", CubeListBuilder.create()
                        .texOffs(32, 32).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(6.0F, -9.0F, 0.0F));

        PartDefinition brazo_derecho = torso.addOrReplaceChild("brazo_derecho", CubeListBuilder.create()
                        .texOffs(58, 56).addBox(0.0F, -4.0F, 2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 31).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 43).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-6.0F, -9.0F, 0.0F));

        brazo_derecho.addOrReplaceChild("cube_r5", CubeListBuilder.create()
                        .texOffs(48, 56).addBox(-2.0F, -2.0F, 1.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        modelPartData.addOrReplaceChild("pierna_izquierda", CubeListBuilder.create()
                        .texOffs(16, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, 12.0F, 2.0F));

        modelPartData.addOrReplaceChild("pierna_derecha", CubeListBuilder.create()
                        .texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, 12.0F, 2.0F));

        return LayerDefinition.create(modelData, 128, 128);
    }

    @Override
    public void setupAnim(@NotNull ShamblerRenderState state) {
        super.setupAnim(state);
        this.setHeadAngle(state.yRot, state.xRot);

        this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks);
        this.walkingAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2.0F, 2.5F);
        this.roaringAnimation.apply(state.roaringAnimationState, state.ageInTicks);
        this.attackingAnimation.apply(state.attackingAnimationState, state.ageInTicks);

        if (state.deathTime > 0.0F) {
            this.deathAnimation.apply((long) (state.deathTime * 50.0F), 1.0F);
        }
    }

    private void setHeadAngle(float yaw, float pitch) {
        this.cabeza_completa.xRot = pitch * ((float) Math.PI / 180F);
        this.cabeza_completa.yRot = yaw * ((float) Math.PI / 180F);
    }
}
