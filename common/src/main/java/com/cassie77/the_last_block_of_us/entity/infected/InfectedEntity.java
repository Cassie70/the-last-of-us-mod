package com.cassie77.the_last_block_of_us.entity.infected;

import com.cassie77.the_last_block_of_us.ModEntities;
import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class InfectedEntity extends Monster implements VibrationSystem {

    private static final EntityDataAccessor<Integer> ANGER =
            SynchedEntityData.defineId(InfectedEntity.class, EntityDataSerializers.INT);

    private static final int ANGRINESS_AMOUNT = 50;
    private static final double CALLING_DISTANCE = 5.0F;

    private final DynamicGameEventListener<Listener> dynamicGameEventListener =
            new DynamicGameEventListener<>(new Listener(this));
    private final User vibrationUser = new VibrationUser();
    private Data vibrationData = new Data();
    AngerManagement angerManagement = new AngerManagement(this::isValidTarget, Collections.emptyList());

    public InfectedEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }
    

    @Override
    public boolean checkSpawnObstruction(@NotNull LevelReader world) {
        return super.checkSpawnObstruction(world) && world.noCollision(this, this.getType().getDimensions().makeBoundingBox(this.position()));
    }

    public abstract SoundEvent getAmbientSound(InfectedAngriness angriness);

    public abstract SoundEvent getListeningSound(InfectedAngriness angriness);

    public abstract SoundEvent getSniffSound();

    public abstract SoundEvent getAngrySound();

    @Override
    public float getWalkTargetValue(@NotNull BlockPos pos, @NotNull LevelReader world) {
        return 0.0F;
    }

    @Override
    protected boolean canRide(@NotNull Entity entity) {
        return false;
    }

    @Override
    protected float getSoundVolume() {
        return 2.0F;
    }

    @Override
    public void playAmbientSound() {
        super.playSound(this.getAmbientSound(this.getAngriness()), 1.5F, 1.0F);
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, @NotNull Entity target) {
        world.broadcastEntityEvent(this, (byte) 4);
        this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.5F, this.getVoicePitch());
        return super.doHurtTarget(world, target);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANGER, 0);
    }

    private void updateAnger() {
        this.entityData.set(ANGER, this.getAngerAtTarget());
    }

    @Override
    protected void customServerAiStep(@NotNull ServerLevel world) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("ClickerBrain");
        this.getBrain().tick(world, this);
        profiler.pop();
        super.customServerAiStep(world);

        if (this.tickCount % 20 == 0) {
            this.angerManagement.tick(world, this::isValidTarget);
            this.updateAnger();

            if (this.getAngerAtTarget() < 135) {
                world.broadcastEntityEvent(this, (byte) 6);
            }

            if (this.getAngerAtTarget() < 120) {
                this.angerManagement.clearAnger(Objects.requireNonNull(this.getTarget()));
            }
        }

        InfectedBrain.updateActivities(this);
    }


    @Override
    protected @NotNull Brain<?> makeBrain(@NotNull Dynamic<?> dynamic) {
        return InfectedBrain.create(this, dynamic);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Brain<InfectedEntity> getBrain() {
        return (Brain<InfectedEntity>) super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public void updateDynamicGameEventListener(@NotNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> callback) {
        Level world = this.level();
        if (world instanceof ServerLevel serverWorld) {
            callback.accept(this.dynamicGameEventListener, serverWorld);
        }
    }

    @Contract("null->false")
    public boolean isValidTarget(@Nullable Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            return this.level() == entity.level()
                    && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)
                    && livingEntity.getType() != EntityType.ARMOR_STAND
                    && livingEntity.getType() != ModEntities.CLICKER
                    && livingEntity.getType() != ModEntities.BLOATER
                    && !livingEntity.isInvulnerable()
                    && !livingEntity.isDeadOrDying()
                    && this.level().getWorldBorder().isWithinBounds(livingEntity.getBoundingBox());
        }
        return false;
    }

    protected void addAdditionalSaveData(@NotNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.store("anger", AngerManagement.codec(this::isValidTarget), this.angerManagement);
        valueOutput.store("listener", Data.CODEC, this.vibrationData);
    }

    protected void readAdditionalSaveData(@NotNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.angerManagement = valueInput.read("anger", AngerManagement.codec(this::isValidTarget)).orElseGet(() -> new AngerManagement(this::isValidTarget, Collections.emptyList()));
        this.updateAnger();
        this.vibrationData = valueInput.read("listener", Data.CODEC).orElseGet(Data::new);
    }

    public InfectedAngriness getAngriness() {
        return InfectedAngriness.getForAnger(this.getAngerAtTarget());
    }

    private int getAngerAtTarget() {
        return this.angerManagement.getActiveAnger(this.getTarget());
    }

    public void removeSuspect(Entity entity) {
        this.angerManagement.clearAnger(entity);
    }

    public void increaseAngerAt(@Nullable Entity entity) {
        this.increaseAngerAt(entity, ANGRINESS_AMOUNT, true);
    }

    @VisibleForTesting
    public void increaseAngerAt(@Nullable Entity entity, int amount, boolean listening) {
        if (!this.isNoAi() && this.isValidTarget(entity)) {
            boolean bl = !(this.getTarget() instanceof Player);
            int i = this.angerManagement.increaseAnger(entity, amount);
            if (entity instanceof Player && bl && InfectedAngriness.getForAnger(i).isAngry()) {
                this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }
        }
    }

    public Optional<LivingEntity> getPrimeSuspect() {
        return this.getAngriness().isAngry() ? this.angerManagement.getActiveEntity() : Optional.empty();
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        return this.getTargetFromBrain();
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel world, @NotNull DamageSource source, float amount) {
        boolean bl = super.hurtServer(world, source, amount);
        if (!this.isNoAi()) {
            Entity entity = source.getEntity();
            this.increaseAngerAt(entity, InfectedAngriness.ANGRY.getThreshold() + 20, false);

            if (entity != null) {
                double radius = CALLING_DISTANCE;
                List<InfectedEntity> nearbyClickers = world.getEntitiesOfClass(
                        InfectedEntity.class,
                        this.getBoundingBox().inflate(radius),
                        e -> e != this && e.isAlive() && !e.isNoAi()
                );

                List<BloaterEntity> nearbyBloaters = world.getEntitiesOfClass(
                        BloaterEntity.class,
                        this.getBoundingBox().inflate(radius),
                        e -> e.isAlive() && !e.isNoAi()
                );

                for (InfectedEntity clicker : nearbyClickers)
                    clicker.increaseAngerAt(entity, InfectedAngriness.ANGRY.getThreshold() + 10, false);

                for (BloaterEntity bloater : nearbyBloaters)
                    bloater.increaseAngerAt(entity, InfectedAngriness.ANGRY.getThreshold() + 10, false);
            }

            if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity livingEntity) {
                if (source.isDirect() || this.closerThan(livingEntity, 5.0)) {
                    this.updateAttackTarget(livingEntity);
                }
            }
        }

        return bl;
    }

    @Override
    public void tick() {
        Level world = this.level();
        if (world instanceof ServerLevel serverWorld) {
            VibrationSystem.Ticker.tick(serverWorld, this.getVibrationData(), this.getVibrationUser());
        }
        super.tick();
    }

    public void updateAttackTarget(LivingEntity target) {
        this.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
        this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        this.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public boolean isPushable() {
        return super.isPushable();
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
        if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
            this.increaseAngerAt(entity);
            InfectedBrain.lookAtDisturbance(this, entity.blockPosition());
        }

        super.doPush(entity);
    }

    @Override
    public @NotNull Data getVibrationData() {
        return this.vibrationData;
    }

    @Override
    public @NotNull User getVibrationUser() {
        return this.vibrationUser;
    }

    class VibrationUser implements User {
        private static final int RANGE = 16;
        private final PositionSource positionSource = new EntityPositionSource(InfectedEntity.this, InfectedEntity.this.getEyeHeight());

        VibrationUser() {
        }

        @Override
        public int getListenerRadius() {
            return RANGE;
        }

        @Override
        public @NotNull PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public @NotNull TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.WARDEN_CAN_LISTEN;
        }

        @Override
        public boolean canReceiveVibration(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull Holder<GameEvent> event, @Nullable GameEvent.Context context) {
            if (!InfectedEntity.this.isNoAi() && !InfectedEntity.this.isDeadOrDying() && !InfectedEntity.this.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN) && world.getWorldBorder().isWithinBounds(pos)) {
                if (context != null) {
                    Entity sourceEntity = context.sourceEntity();
                    if (sourceEntity instanceof LivingEntity livingEntity) {
                        return InfectedEntity.this.isValidTarget(livingEntity);
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onReceiveVibration(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull Holder<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            if (!InfectedEntity.this.isDeadOrDying()) {
                InfectedEntity.this.brain.setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 20L);
                world.broadcastEntityEvent(InfectedEntity.this, (byte) 61);
                if (!InfectedEntity.this.hasPose(Pose.ROARING)) {
                    InfectedEntity.this.playSound(InfectedEntity.this.getListeningSound(InfectedEntity.this.getAngriness()), 1.0F, InfectedEntity.this.getVoicePitch());
                }
                BlockPos blockPos = pos;
                if (entity != null) {
                    if (InfectedEntity.this.closerThan(entity, 30.0)) {
                        if (InfectedEntity.this.getBrain().hasMemoryValue(MemoryModuleType.RECENT_PROJECTILE)) {
                            if (InfectedEntity.this.isValidTarget(entity)) {
                                blockPos = entity.blockPosition();
                            }

                            InfectedEntity.this.increaseAngerAt(entity);
                        } else {
                            InfectedEntity.this.increaseAngerAt(entity, 10, true);
                        }
                    }

                    InfectedEntity.this.getBrain().setMemoryWithExpiry(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
                } else {
                    InfectedEntity.this.increaseAngerAt(sourceEntity);
                }

                if (!InfectedEntity.this.getAngriness().isAngry()) {
                    Optional<LivingEntity> optional = InfectedEntity.this.angerManagement.getActiveEntity();
                    if (entity != null || optional.isEmpty() || optional.get() == sourceEntity) {
                        InfectedBrain.lookAtDisturbance(InfectedEntity.this, blockPos);
                    }
                }
            }
        }
    }
}
