package com.cassie77.the_last_block_of_us.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBombEntity extends ThrowableItemProjectile {

    private static final EntityDataAccessor<Boolean> IN_GROUND =
            SynchedEntityData.defineId(AbstractBombEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final int DEFAULT_FUSE_TICKS = 60; // 3 segundos

    private int ticksOnGround = 0;
    @Nullable
    private BlockState lastState;
    @Nullable
    private BlockPos landedPos;

    protected AbstractBombEntity(EntityType<? extends AbstractBombEntity> entityType, Level world) {
        super(entityType, world);
    }

    protected AbstractBombEntity(EntityType<? extends AbstractBombEntity> entityType, LivingEntity owner, Level world, ItemStack stack) {
        super(entityType, owner, world, stack);
    }

    protected AbstractBombEntity(EntityType<? extends AbstractBombEntity> entityType, double x, double y, double z, Level world, ItemStack stack) {
        super(entityType, x, y, z, world, stack);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IN_GROUND, false);
    }

    public boolean isInGround() {
        return this.entityData.get(IN_GROUND);
    }

    public void setInGround(boolean inGround) {
        this.entityData.set(IN_GROUND, inGround);
    }

    protected int getFuseTicks() {
        return DEFAULT_FUSE_TICKS;
    }

    @Override
    public void tick() {
        if (this.isInGround()) {
            this.setDeltaMovement(Vec3.ZERO);

            if (!this.level().isClientSide()) {
                if (this.landedPos != null) {
                    BlockState currentState = this.level().getBlockState(this.landedPos);
                    if (currentState != this.lastState && this.shouldFall()) {
                        this.startFalling();
                        super.tick();
                        return;
                    }
                }

                this.ticksOnGround++;
                if (this.ticksOnGround >= this.getFuseTicks()) {
                    this.detonate();
                }
            }
        } else {
            if (!this.level().isClientSide() && this.ticksOnGround > 0) {
                this.ticksOnGround++;
                if (this.ticksOnGround >= this.getFuseTicks()) {
                    this.detonate();
                    return;
                }
            }
            super.tick();
        }
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        if (this.isInGround()) return;

        if (hitResult instanceof EntityHitResult entityHitResult) {
            this.onHitEntity(entityHitResult);
            return;
        }

        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState state = this.level().getBlockState(blockPos);

            if (state.isAir()) return;

            if (this.onBeforeBlockImpact(blockPos, state)) {
                return;
            }

            Direction face = blockHitResult.getDirection();

            if (face == Direction.UP) {
                this.landOnGround(blockHitResult, blockPos, state);
            } else {
                Vec3 vel = this.getDeltaMovement();
                if (face.getAxis() == Direction.Axis.X) {
                    vel = new Vec3(-vel.x * 0.2, Math.min(vel.y * 0.5, -0.05), vel.z * 0.3);
                } else if (face.getAxis() == Direction.Axis.Z) {
                    vel = new Vec3(vel.x * 0.3, Math.min(vel.y * 0.5, -0.05), -vel.z * 0.2);
                } else {
                    vel = new Vec3(vel.x * 0.4, -0.1, vel.z * 0.4);
                }
                this.setDeltaMovement(vel);
                this.hasImpulse = true;
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        if (!this.level().isClientSide()) {
            Entity entity = entityHitResult.getEntity();

            if (this.level() instanceof ServerLevel serverWorld) {
                entity.hurtServer(serverWorld, this.damageSources().generic(), this.getDirectHitDamage());
            }

            if (entity instanceof LivingEntity livingEntity) {
                this.applyDirectHitEffects(livingEntity);
            }
        }

        Vec3 vel = this.getDeltaMovement();
        this.setDeltaMovement(vel.x * -0.15, -0.1, vel.z * -0.15);
        this.hasImpulse = true;
        if (this.ticksOnGround == 0) {
            this.ticksOnGround = 1;
        }
    }

    protected boolean onBeforeBlockImpact(BlockPos pos, BlockState state) {
        return false;
    }

    protected abstract float getDirectHitDamage();

    protected abstract void applyDirectHitEffects(LivingEntity target);

    protected abstract void onDetonate(ServerLevel serverLevel);

    private void detonate() {
        if (this.level() instanceof ServerLevel serverLevel) {
            this.onDetonate(serverLevel);
        }
        this.discard();
    }

    private void landOnGround(BlockHitResult hitResult, BlockPos blockPos, BlockState state) {
        this.setPos(hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z);
        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = true;
        this.landedPos = blockPos;
        this.lastState = state;
        this.setInGround(true);

        if (!this.level().isClientSide()) {
            this.gameEvent(GameEvent.HIT_GROUND);
            this.level().broadcastEntityEvent(this, (byte) 3);
        }
    }

    private boolean shouldFall() {
        AABB checkBox = this.getBoundingBox().expandTowards(0.0, -0.08, 0.0);
        return this.level().noCollision(this, checkBox);
    }

    private void startFalling() {
        this.setInGround(false);
        this.landedPos = null;
        this.lastState = null;
        this.setDeltaMovement(0.0, -0.05, 0.0);
        this.hasImpulse = true;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putShort("TicksOnGround", (short) this.ticksOnGround);
        output.putBoolean("InGround", this.isInGround());
        output.storeNullable("InBlockState", BlockState.CODEC, this.lastState);
        output.storeNullable("LandedPos", BlockPos.CODEC, this.landedPos);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.ticksOnGround = input.getShortOr("TicksOnGround", (short) 0);
        this.setInGround(input.getBooleanOr("InGround", false));
        this.lastState = input.read("InBlockState", BlockState.CODEC).orElse(null);
        this.landedPos = input.read("LandedPos", BlockPos.CODEC).orElse(null);
    }
}