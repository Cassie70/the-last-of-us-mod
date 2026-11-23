package com.cassie77.the_last_block_of_us.item.molotov;

import com.cassie77.the_last_block_of_us.ModEntities;
import com.cassie77.the_last_block_of_us.ModItems;
import com.cassie77.the_last_block_of_us.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MolotovEntity extends ThrowableItemProjectile {
    private int glassHits = 0;

    public MolotovEntity(EntityType<? extends MolotovEntity> entityType, Level world) {
        super(entityType, world);
    }

    public MolotovEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.MOLOTOV_ENTITY, owner, world, stack);
    }

    public MolotovEntity(Level world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.MOLOTOV_ENTITY, x, y, z, world, stack);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.MOLOTOV;
    }

    private ParticleOptions getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, itemStack);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 3) {
            ParticleOptions particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);

            if (hitResult instanceof BlockHitResult blockHitResult) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                Block block = this.level().getBlockState(blockPos).getBlock();

                if (block == Blocks.AIR) return;

                if (isGlass(block)) {
                    if (glassHits >= 3) {
                        breakMolotov(blockHitResult);
                        return;
                    }
                    glassHits++;
                    this.level().destroyBlock(blockPos, false);
                    return;
                }

                breakMolotov(blockHitResult);
            }
        }
    }

    private void breakMolotov(@Nullable BlockHitResult hitResult) {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.LAVA,
                    this.getX(),
                    this.getY() + 0.2,
                    this.getZ(),
                    100,
                    0.5, 0.5, 0.5,
                    0.0
            );
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.level().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.EXPLODE_MOLOTOV,
                    SoundSource.NEUTRAL,
                    2.0F, 1.0F
            );
            this.gameEvent(GameEvent.EXPLODE);
            spawnMolotovFire(hitResult);
        }
        this.discard();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);

        if (!this.level().isClientSide()) {
            Entity entity = entityHitResult.getEntity();

            float damageAmount = 15.0F;
            if (this.level() instanceof ServerLevel serverWorld) {
                entity.hurtServer(serverWorld, this.damageSources().generic(), damageAmount);
            }
            entity.igniteForSeconds(20);

            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 7, false, true));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 5, false, true));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 5, false, true));
            }

            breakMolotov(null);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            double offsetX = (this.random.nextDouble() - 0.5) * 0.2;
            double offsetY = (this.random.nextDouble() - 0.5) * 0.2;
            double offsetZ = (this.random.nextDouble() - 0.5) * 0.2;

            this.level().addParticle(
                    ParticleTypes.FLAME,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0.0, 0.0, 0.0
            );
        }
    }

    private void spawnMolotovFire(@Nullable BlockHitResult hitResult) {
        Level level = this.level();
        BlockPos startPos;

        if (hitResult != null) {
            Vec3 normal = Vec3.atLowerCornerOf(hitResult.getDirection().getUnitVec3i());
            Vec3 safePoint = hitResult.getLocation().add(normal.scale(0.25));
            startPos = BlockPos.containing(safePoint);
        } else {
            startPos = this.blockPosition();
        }

        BlockPos groundCenter = startPos;
        int maxDrop = 12;
        while (maxDrop > 0 && groundCenter.getY() > level.getMinY() && canFireReplace(level, groundCenter)) {
            groundCenter = groundCenter.below();
            maxDrop--;
        }

        BlockPos center = groundCenter.above();

        int radius = 3;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;

                for (int dy = 2; dy >= -4; dy--) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockPos belowPos = pos.below();

                    BlockState targetState = level.getBlockState(pos);
                    BlockState belowState = level.getBlockState(belowPos);

                    if (!targetState.getFluidState().isEmpty() || !belowState.getFluidState().isEmpty()) {
                        break;
                    }

                    if (canFireReplace(level, pos) && !canFireReplace(level, belowPos)) {
                        BlockState fireState = BaseFireBlock.getState(level, pos);
                        if (fireState.canSurvive(level, pos)) {
                            level.setBlockAndUpdate(pos, fireState);
                        }
                        break;
                    }
                }
            }
        }
    }

    private boolean canFireReplace(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || (state.canBeReplaced() && state.getFluidState().isEmpty());
    }

    private boolean isGlass(Block block) {
        return block == Blocks.GLASS_PANE || block == Blocks.GLASS
                || block instanceof StainedGlassBlock || block instanceof StainedGlassPaneBlock;
    }
}
