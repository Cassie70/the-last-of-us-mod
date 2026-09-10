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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

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
                BlockState blockState = this.level().getBlockState(blockPos);

                if(blockState.getBlock() == Blocks.AIR){
                    return;
                }

                if (blockState.getBlock() == Blocks.GLASS_PANE || blockState.getBlock() == Blocks.GLASS) {
                    if (glassHits >= 3) {
                        breakMolotov();
                        return;
                    }
                    glassHits++;

                    this.level().destroyBlock(blockPos, false);

                }else{
                    breakMolotov();
                }
            }


        }
    }

    private void breakMolotov() {
        ((ServerLevel) this.level()).sendParticles(
                ParticleTypes.LAVA,
                this.getX() + 0.5,
                this.getY() + 0.5,
                this.getZ() + 0.5,
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
        spawnMolotovFire();
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

    private void spawnMolotovFire() {
        BlockPos center = this.blockPosition();

        while (center.getY() > this.level().getMinY() &&
                this.level().getBlockState(center).isAir()) {
            center = center.below();
        }
        center = center.above();

        int radius = 3;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius) {
                    for (int dy = -1; dy <= 1; dy++) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        if (!this.level().getBlockState(pos.below()).isAir() &&
                                this.level().getBlockState(pos).isAir()) {
                            this.level().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                        }
                    }
                }
            }
        }
    }
}
