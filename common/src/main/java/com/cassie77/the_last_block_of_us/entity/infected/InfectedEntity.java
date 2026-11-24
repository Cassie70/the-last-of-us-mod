package com.cassie77.the_last_block_of_us.entity.infected;

import com.cassie77.the_last_block_of_us.ModEffects;
import com.cassie77.the_last_block_of_us.ModEntities;
import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class InfectedEntity extends Monster implements VibrationSystem {

    private static final EntityDataAccessor<Integer> ANGER =
            SynchedEntityData.defineId(InfectedEntity.class, EntityDataSerializers.INT);

    private final DynamicGameEventListener<Listener> dynamicGameEventListener =
            new DynamicGameEventListener<>(new Listener(this));
    private final User vibrationUser = new VibrationUser();
    private Data vibrationData = new Data();
    AngerManagement angerManagement = new AngerManagement(this::isValidTarget, Collections.emptyList());

    public InfectedEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    protected float getInfectedStrollSpeed() { return 0.8F; }
    protected float getInfectedCelebrateTime() { return 0.7F; }
    protected float getInfectedRangedApproachSpeed() { return 1.2F; }
    protected int getInfectedMeleeAttackInterval() { return 10; }
    protected int getInfectedRoarDuration() { return 30; }
    protected int getInfectedSniffDuration() { return 60; }
    protected float getInfectedSwimSpeed() { return 0.8F; }
    protected int getInfectedLookAtTargetMinDuration() { return 45; }
    protected int getInfectedLookAtTargetMaxDuration() { return 90; }
    protected double getInfectedSniffHorizontalRadius() { return 4; }
    protected double getInfectedSniffVerticalRadius() { return 4; }
    protected int getInfectedAngrinessAmount() { return 50; }
    protected double getInfectedCallingDistance() { return 5.0F; }

    protected BehaviorControl<? super InfectedEntity> getAdditionalFightTask() {
        return null;
    }

    /**
     * Whether this infected fights with the default melee attack and walk-towards-target
     * behaviours. Mobs with a custom fight task (for example the Shambler) can disable
     * them and drive their own movement.
     */
    protected boolean usesMeleeCombat() {
        return true;
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
    public boolean doHurtTarget(@NotNull Entity target) {
        this.level().broadcastEntityEvent(this, (byte) 4);
        this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.5F, this.getVoicePitch());
        return super.doHurtTarget(target);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANGER, 0);
    }

    private void updateAnger() {
        this.entityData.set(ANGER, this.getAngerAtTarget());
    }

    @Override
    protected void customServerAiStep() {
        ServerLevel world = (ServerLevel) this.level();
        ProfilerFiller profiler = world.getProfiler();
        profiler.push("ClickerBrain");
        this.getBrain().tick(world, this);
        profiler.pop();
        super.customServerAiStep();

        if (this.tickCount % 10 == 0) {
            this.angerManagement.tick(world, this::isValidTarget);
            this.updateAnger();

            if (this.getAngerAtTarget() < 135) {
                world.broadcastEntityEvent(this, (byte) 6);
            }

            if (this.getAngerAtTarget() < 120) {
                this.angerManagement.clearAnger(this.getTarget());
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
                    && livingEntity.getType() != ModEntities.SHAMBLER
                    && !livingEntity.canBreatheUnderwater()
                    && !livingEntity.isInvulnerable()
                    && !livingEntity.isDeadOrDying()
                    && this.level().getWorldBorder().isWithinBounds(livingEntity.getBoundingBox());
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        AngerManagement.codec(this::isValidTarget).encodeStart(NbtOps.INSTANCE, this.angerManagement)
                .result().ifPresent(encoded -> tag.put("anger", encoded));
        Data.CODEC.encodeStart(NbtOps.INSTANCE, this.vibrationData)
                .result().ifPresent(encoded -> tag.put("listener", encoded));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.angerManagement = AngerManagement.codec(this::isValidTarget)
                .parse(NbtOps.INSTANCE, tag.get("anger"))
                .result().orElseGet(() -> new AngerManagement(this::isValidTarget, Collections.emptyList()));
        this.updateAnger();
        this.vibrationData = Data.CODEC.parse(NbtOps.INSTANCE, tag.get("listener"))
                .result().orElseGet(Data::new);
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
        this.increaseAngerAt(entity, this.getInfectedAngrinessAmount());
    }

    @VisibleForTesting
    public void increaseAngerAt(@Nullable Entity entity, int amount) {
        if (!this.isNoAi() && this.isValidTarget(entity)) {
            boolean bl = !(this.getTarget() instanceof Player);
            int i = this.angerManagement.increaseAnger(entity, amount);
            if (entity instanceof Player && bl && InfectedAngriness.getForAnger(i).isAngry()) {
                this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == ModEffects.CORROSION) {
            return false;
        }

        return super.canBeAffected(effect);
    }

    public Optional<LivingEntity> getPrimeSuspect() {
        return this.getAngriness().isAngry() ? this.angerManagement.getActiveEntity() : Optional.empty();
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        return this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        boolean bl = super.hurt(source, amount);
        if (!this.isNoAi() && this.level() instanceof ServerLevel world) {
            Entity entity = source.getEntity();
            this.increaseAngerAt(entity, InfectedAngriness.ANGRY.getThreshold() + 20);

            if (entity != null) {
                double radius = this.getInfectedCallingDistance();
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
                    clicker.increaseAngerAt(entity, InfectedAngriness.ANGRY.getThreshold() + 10);

                for (BloaterEntity bloater : nearbyBloaters)
                    bloater.increaseAngerAt(entity, InfectedAngriness.ANGRY.getThreshold() + 10);
            }

            if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity livingEntity) {
                if (source.getDirectEntity() != null || this.closerThan(livingEntity, 5.0)) {
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
        public boolean canReceiveVibration(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull GameEvent event, @Nullable GameEvent.Context context) {
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
        public void onReceiveVibration(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull GameEvent event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
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
                            InfectedEntity.this.increaseAngerAt(entity, 10);
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
