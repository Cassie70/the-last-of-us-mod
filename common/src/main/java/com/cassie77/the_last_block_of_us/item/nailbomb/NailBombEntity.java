package com.cassie77.the_last_block_of_us.item.nailbomb;

import com.cassie77.the_last_block_of_us.ModEntities;
import com.cassie77.the_last_block_of_us.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class NailBombEntity extends ThrowableItemProjectile {

    private int ticksOnGround = 0;
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
                        breakNailBomb();
                        return;
                    }
                    glassHits++;

                    this.level().destroyBlock(blockPos, false);

                }else{
                    breakNailBomb();
                }
            }
        }
    }

    private void breakNailBomb() {

        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = true;

        ticksOnGround++;
        if (ticksOnGround > 60) {
            this.setPos(this.getX(), this.getBlockY() + 2, this.getZ());


            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);

            ((ServerLevel) this.level()).sendParticles(
                    ParticleTypes.EXPLOSION,
                    this.getX() + 0.5,
                    this.getY() + 0.5,
                    this.getZ() + 0.5,
                    50,
                    2, 2, 2,
                    0.0
            );

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 5.0F, Level.ExplosionInteraction.NONE);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);

        if (!this.level().isClientSide()) {
            Entity entity = entityHitResult.getEntity();

            float damageAmount = 5.0F;
            if (this.level() instanceof ServerLevel serverWorld) {
                entity.hurtServer(serverWorld, this.damageSources().generic(), damageAmount);
            }

            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 7, false, true));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 5, false, true));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 5, false, true));
            }
        }
    }
}
