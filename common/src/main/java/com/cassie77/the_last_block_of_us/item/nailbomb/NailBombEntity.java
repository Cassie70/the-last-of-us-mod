package com.cassie77.the_last_block_of_us.item.nailbomb;

import com.cassie77.the_last_block_of_us.ModEntities;
import com.cassie77.the_last_block_of_us.ModItems;
import com.cassie77.the_last_block_of_us.item.AbstractBombEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class NailBombEntity extends AbstractBombEntity {

    private int glassHits = 0;

    public NailBombEntity(EntityType<? extends NailBombEntity> entityType, Level world) {
        super(entityType, world);
    }

    public NailBombEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.NAIL_BOMB_ENTITY, owner, world, stack);
    }

    public NailBombEntity(Level world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.NAIL_BOMB_ENTITY, x, y, z, world, stack);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.NAIL_BOMB;
    }

    @Override
    protected boolean onBeforeBlockImpact(BlockPos pos, BlockState state) {
        if (isGlass(state.getBlock()) && this.glassHits < 3) {
            if (!this.level().isClientSide()) {
                this.glassHits++;
                this.level().destroyBlock(pos, false);
            }
            return true;
        }
        return false;
    }

    @Override
    protected float getDirectHitDamage() {
        return 5.0F;
    }

    @Override
    protected void applyDirectHitEffects(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 7, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 5, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 5, false, true));
    }

    @Override
    protected void onDetonate(ServerLevel serverLevel) {
        double expY = this.getY() + 0.5;

        serverLevel.playSound(null, this.getX(), expY, this.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);

        serverLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                this.getX(),
                expY,
                this.getZ(),
                50,
                2.0, 2.0, 2.0,
                0.0
        );

        serverLevel.explode(this, this.getX(), expY, this.getZ(), 5.0F, Level.ExplosionInteraction.NONE);
    }

    private boolean isGlass(Block block) {
        return block == Blocks.GLASS_PANE || block == Blocks.GLASS
                || block instanceof StainedGlassBlock || block instanceof StainedGlassPaneBlock;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putByte("GlassHits", (byte) this.glassHits);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.glassHits = input.getByteOr("GlassHits", (byte) 0);
    }
}