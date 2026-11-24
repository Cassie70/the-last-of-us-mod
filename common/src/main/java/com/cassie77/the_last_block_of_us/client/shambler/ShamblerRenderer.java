package com.cassie77.the_last_block_of_us.client.shambler;

import com.cassie77.the_last_block_of_us.Constants;
import com.cassie77.the_last_block_of_us.entity.shambler.ShamblerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ShamblerRenderer extends MobRenderer<ShamblerEntity, ShamblerModel> {

    public ShamblerRenderer(EntityRendererProvider.Context context) {
        super(context, new ShamblerModel(context.bakeLayer(ShamblerModel.SHAMBLER)), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ShamblerEntity entity) {
        return new ResourceLocation(Constants.MOD_ID, "textures/entity/shambler/shambler.png");
    }

    /**
     * The Shambler has its own DEATH animation, so the vanilla death tilt (and the
     * sleeping flip, which never applies to this mob) is disabled to avoid stacking
     * both rotations.
     */
    @Override
    protected float getFlipDegrees(@NotNull ShamblerEntity entity) {
        return 0.0F;
    }
}
