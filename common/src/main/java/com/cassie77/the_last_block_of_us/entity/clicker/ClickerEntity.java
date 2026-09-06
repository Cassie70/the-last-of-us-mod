package com.cassie77.the_last_block_of_us.entity.clicker;

import com.cassie77.the_last_block_of_us.ModEntities;
import com.cassie77.the_last_block_of_us.ModSounds;
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
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ClickerEntity extends Monster implements VibrationSystem {

    private static final EntityDataAccessor<Integer> ANGER =
            SynchedEntityData.defineId(ClickerEntity.class, EntityDataSerializers.INT);

    private static final double MAX_HEALTH = 40.0;
    private static final double MOVE_SPEED = 0.3;
    private static final double KNOCKBACK_RESISTANCE = 0.0;
    private static final double ATTACK_KNOCKBACK = 1.0;
    private static final double ATTACK_DAMAGE = 15.0;
    private static final double FOLLOW_RANGE = 8.0;
    private static final int ANGRINESS_AMOUNT = 50;
    private static final int WEAPON_DISABLE_BLOCKING_SECONDS = 3;
    private static final double CALLING_DISTANCE = 3.0F;

    private static final Predicate<Difficulty> DOOR_BREAK_DIFFICULTY_CHECKER = (difficulty) -> difficulty == Difficulty.HARD;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState roaringAnimationState = new AnimationState();

    private final ClickerBreakDoorGoal breakDoorsGoal;

    private final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener =
            new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
    private final VibrationSystem.User vibrationUser = new ClickerEntity.VibrationUser();
    private VibrationSystem.Data vibrationData = new VibrationSystem.Data();
    AngerManagement angerManagement = new AngerManagement(this::isValidTarget, Collections.emptyList());

    private boolean canBreakDoors;

    public ClickerEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);

        this.xpReward = 20;
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_OTHER, 8.0F);
        this.setPathfindingMalus(PathType.POWDER_SNOW, 8.0F);
        this.setPathfindingMalus(PathType.LAVA, 8.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);

        this.breakDoorsGoal = new ClickerBreakDoorGoal(this, DOOR_BREAK_DIFFICULTY_CHECKER);
    }

    public boolean canBreakDoors() {
        return this.canBreakDoors;
    }

    public void setCanBreakDoors(boolean canBreakDoors) {
        if (this.getNavigation() instanceof GroundPathNavigation) {
            if (this.canBreakDoors != canBreakDoors) {
                this.canBreakDoors = canBreakDoors;
                this.getNavigation().setCanOpenDoors(canBreakDoors);
                if (canBreakDoors) {
                    this.goalSelector.addGoal(1, this.breakDoorsGoal);
                } else {
                    this.goalSelector.removeGoal(this.breakDoorsGoal);
                }
            }
        } else if (this.canBreakDoors) {
            this.goalSelector.removeGoal(this.breakDoorsGoal);
            this.canBreakDoors = false;
        }
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader world) {
        return super.checkSpawnObstruction(world) && world.noCollision(this, this.getType().getDimensions().makeBoundingBox(this.position()));
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader world) {
        return 0.0F;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    public float getSecondsToDisableBlocking() {
        return WEAPON_DISABLE_BLOCKING_SECONDS;
    }

    public static AttributeSupplier.Builder addAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVE_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
                .add(Attributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE);
    }

    @Override
    protected float getSoundVolume() {
        return 2.0F;
    }

    @Override
    public void playAmbientSound() {
        super.playSound(this.getAngriness().getSound(), 1.5F, 1.0F);
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        world.broadcastEntityEvent(this, (byte) 4);
        this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.5F, this.getVoicePitch());
        return super.doHurtTarget(world, target);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANGER, 0);
    }

    private void updateAnger() {
        this.entityData.set(ANGER, this.getAngerAtTarget());
    }

    @Override
    public void tick() {
        Level world = this.level();
        if (world instanceof ServerLevel serverWorld) {
            VibrationSystem.Ticker.tick(serverWorld, this.vibrationData, this.vibrationUser);
        }

        if (this.getPose() == Pose.STANDING
                && !this.idleAnimationState.isStarted()
                && !this.attackingAnimationState.isStarted()
                && !this.roaringAnimationState.isStarted()) {
            this.idleAnimationState.start(this.tickCount);
        }

        super.tick();
    }

    @Override
    protected void customServerAiStep(ServerLevel world) {
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
                this.angerManagement.clearAnger(this.getTarget());
            }
        }

        ClickerBrain.updateActivities(this);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 4) {
            this.roaringAnimationState.stop();
            this.attackingAnimationState.start(this.tickCount);
        } else if (status == 5) {
            this.roaringAnimationState.stop();
            this.attackingAnimationState.start(this.tickCount);
        } else if (status == 6) {
            this.attackingAnimationState.stop();
            if (!this.idleAnimationState.isStarted()) {
                this.idleAnimationState.start(this.tickCount);
            }
        } else {
            super.handleEntityEvent(status);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (DATA_POSE.equals(data)) {
            switch (this.getPose()) {
                case ROARING -> this.roaringAnimationState.start(this.tickCount);
                case SNIFFING -> {
                    this.roaringAnimationState.stop();
                    this.roaringAnimationState.start(this.tickCount);
                }
                case STANDING -> {
                    this.roaringAnimationState.stop();
                    this.idleAnimationState.start(this.tickCount);
                }
                default -> {}
            }
        }

        super.onSyncedDataUpdated(data);
    }

    @Override
    protected @NotNull Brain<?> makeBrain(Dynamic<?> dynamic) {
        return ClickerBrain.create(this, dynamic);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Brain<ClickerEntity> getBrain() {
        return (Brain<ClickerEntity>) super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> callback) {
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

    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.store("anger", AngerManagement.codec(this::isValidTarget), this.angerManagement);
        valueOutput.putBoolean("CanBreakDoors", this.canBreakDoors());
        valueOutput.store("listener", Data.CODEC, this.vibrationData);
    }

    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.angerManagement = valueInput.read("anger", AngerManagement.codec(this::isValidTarget)).orElseGet(() -> new AngerManagement(this::isValidTarget, Collections.emptyList()));
        this.setCanBreakDoors(valueInput.getBooleanOr("CanBreakDoors", false));
        this.updateAnger();
        this.vibrationData = valueInput.read("listener", Data.CODEC).orElseGet(Data::new);
    }

    public ClickerAngriness getAngriness() {
        return ClickerAngriness.getForAnger(this.getAngerAtTarget());
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
            if (entity instanceof Player && bl && ClickerAngriness.getForAnger(i).isAngry()) {
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

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        this.setCanBreakDoors(true);
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        boolean bl = super.hurtServer(world, source, amount);
        if (!this.isNoAi()) {
            Entity entity = source.getEntity();
            this.increaseAngerAt(entity, ClickerAngriness.ANGRY.getThreshold() + 20, false);

            if (entity != null) {
                double radius = CALLING_DISTANCE;
                List<ClickerEntity> nearbyClickers = world.getEntitiesOfClass(
                        ClickerEntity.class,
                        this.getBoundingBox().inflate(radius),
                        e -> e != this && e.isAlive() && !e.isNoAi()
                );

                List<BloaterEntity> nearbyBloaters = world.getEntitiesOfClass(
                        BloaterEntity.class,
                        this.getBoundingBox().inflate(radius),
                        e -> e.isAlive() && !e.isNoAi()
                );

                for (ClickerEntity clicker : nearbyClickers)
                    clicker.increaseAngerAt(entity, ClickerAngriness.ANGRY.getThreshold() + 10, false);

                for (BloaterEntity bloater : nearbyBloaters)
                    bloater.increaseAngerAt(entity, ClickerAngriness.ANGRY.getThreshold() + 10, false);
            }

            if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity livingEntity) {
                if (source.isDirect() || this.closerThan(livingEntity, 5.0)) {
                    this.updateAttackTarget(livingEntity);
                }
            }
        }

        return bl;
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
    protected void doPush(Entity entity) {
        if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
            this.increaseAngerAt(entity);
            ClickerBrain.lookAtDisturbance(this, entity.blockPosition());
        }

        super.doPush(entity);
    }

    @Override
    public VibrationSystem.@NotNull Data getVibrationData() {
        return this.vibrationData;
    }

    @Override
    public VibrationSystem.@NotNull User getVibrationUser() {
        return this.vibrationUser;
    }

    class VibrationUser implements VibrationSystem.User {
        private static final int RANGE = 16;
        private final PositionSource positionSource = new EntityPositionSource(ClickerEntity.this, ClickerEntity.this.getEyeHeight());

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
        public boolean canReceiveVibration(ServerLevel world, BlockPos pos, Holder<GameEvent> event, @Nullable GameEvent.Context context) {
            if (!ClickerEntity.this.isNoAi() && !ClickerEntity.this.isDeadOrDying() && !ClickerEntity.this.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN) && world.getWorldBorder().isWithinBounds(pos)) {
                if (context != null) {
                    Entity sourceEntity = context.sourceEntity();
                    if (sourceEntity instanceof LivingEntity livingEntity) {
                        return ClickerEntity.this.isValidTarget(livingEntity);
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onReceiveVibration(ServerLevel world, BlockPos pos, Holder<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            if (!ClickerEntity.this.isDeadOrDying()) {
                ClickerEntity.this.brain.setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 20L);
                world.broadcastEntityEvent(ClickerEntity.this, (byte) 61);
                if (!ClickerEntity.this.hasPose(Pose.ROARING)) {
                    ClickerEntity.this.playSound(ModSounds.CLICKER_ALERT, 1.0F, ClickerEntity.this.getVoicePitch());
                }
                BlockPos blockPos = pos;
                if (entity != null) {
                    if (ClickerEntity.this.closerThan(entity, 30.0)) {
                        if (ClickerEntity.this.getBrain().hasMemoryValue(MemoryModuleType.RECENT_PROJECTILE)) {
                            if (ClickerEntity.this.isValidTarget(entity)) {
                                blockPos = entity.blockPosition();
                            }

                            ClickerEntity.this.increaseAngerAt(entity);
                        } else {
                            ClickerEntity.this.increaseAngerAt(entity, 10, true);
                        }
                    }

                    ClickerEntity.this.getBrain().setMemoryWithExpiry(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
                } else {
                    ClickerEntity.this.increaseAngerAt(sourceEntity);
                }

                if (!ClickerEntity.this.getAngriness().isAngry()) {
                    Optional<LivingEntity> optional = ClickerEntity.this.angerManagement.getActiveEntity();
                    if (entity != null || optional.isEmpty() || optional.get() == sourceEntity) {
                        ClickerBrain.lookAtDisturbance(ClickerEntity.this, blockPos);
                    }
                }
            }
        }
    }
}
