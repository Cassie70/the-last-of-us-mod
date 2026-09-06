package com.cassie77.entity.bloater;

import com.cassie77.ModEntities;
import com.cassie77.ModSounds;
import com.cassie77.entity.clicker.ClickerAngriness;
import com.cassie77.entity.clicker.ClickerEntity;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
import java.util.Set;
import java.util.function.BiConsumer;

public class BloaterEntity extends Monster implements VibrationSystem {

    private static final EntityDataAccessor<Integer> ANGER =
            SynchedEntityData.defineId(BloaterEntity.class, EntityDataSerializers.INT);

    private static final double MAX_HEALTH = 300.0;
    private static final double MOVE_SPEED = 0.3;
    private static final double KNOCKBACK_RESISTANCE = 0.75;
    private static final double ATTACK_KNOCKBACK = 2.0;
    private static final double ATTACK_DAMAGE = 20.0;
    private static final double FOLLOW_RANGE = 6.0;
    private static final int ANGRINESS_AMOUNT = 45;
    private static final int WEAPON_DISABLE_BLOCKING_SECONDS = 5;
    private static final double CALLING_RADIUS = 3.0;
    private static final int BREAKING_BLOCK_COOLDOWN = 30;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState roaringAnimationState = new AnimationState();
    public final AnimationState throwingAnimationState = new AnimationState();

    private final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener =
            new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
    private final VibrationSystem.User vibrationUser = new BloaterEntity.VibrationUser();
    private VibrationSystem.Data vibrationData = new VibrationSystem.Data();
    AngerManagement angerManagement = new AngerManagement(this::isValidTarget, Collections.emptyList());

    private int blockBreakingCooldown = 0;

    public BloaterEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);

        this.xpReward = 200;
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_OTHER, 8.0F);
        this.setPathfindingMalus(PathType.POWDER_SNOW, 8.0F);
        this.setPathfindingMalus(PathType.LAVA, 8.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
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
        super.playSound(this.getAngriness().getSound(), 2.0F, 1.0F);
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
        ThrowTask.cooldown(this, 40);
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
        profiler.push("BloaterBrain");
        this.getBrain().tick(world, this);
        profiler.pop();
        super.customServerAiStep(world);

        if (this.tickCount % 20 == 0) {
            this.angerManagement.tick(world, this::isValidTarget);
            this.updateAnger();
        }

        if (this.blockBreakingCooldown <= 0) {
            this.blockBreakingCooldown = BREAKING_BLOCK_COOLDOWN;
        }

        if (this.getAngriness().isAngry()) {
            if (this.blockBreakingCooldown > 0) {
                --this.blockBreakingCooldown;
                if (this.blockBreakingCooldown == 0 && world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    boolean destroyed = false;
                    int j = Mth.floor(this.getBbWidth() / 2.0F + 1.0F);
                    int k = Mth.floor(this.getBbHeight());

                    LivingEntity target = this.getTarget();
                    boolean targetIsBelow = target != null && target.getY() < this.getY();

                    int minY = targetIsBelow ? -1 : 0;

                    for (BlockPos blockPos : BlockPos.betweenClosed(
                            this.getBlockX() - j, this.getBlockY() + minY, this.getBlockZ() - j,
                            this.getBlockX() + j, this.getBlockY() + k,    this.getBlockZ() + j)) {

                        BlockState blockState = world.getBlockState(blockPos);
                        if (canDestroy(blockState)) {
                            destroyed = world.destroyBlock(blockPos, true, this) || destroyed;
                        }
                    }

                    if (destroyed) {
                        world.levelEvent(null, 1022, this.blockPosition(), 0);
                        this.level().broadcastEntityEvent(this, (byte) 5);
                    }
                }
            }
        }
        BloaterBrain.updateActivities(this);
    }

    private static final Set<Block> BLOATER_IMMUNE = Set.of(
            Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS, Blocks.ENCHANTING_TABLE, Blocks.BEACON, Blocks.ENDER_CHEST, Blocks.SHORT_GRASS, Blocks.TALL_GRASS
    );

    public static boolean canDestroy(BlockState block) {
        if (block.is(BlockTags.FLOWERS) || block.is(BlockTags.SMALL_FLOWERS)) return false;
        if (BLOATER_IMMUNE.contains(block.getBlock())) return false;
        return !block.isAir() && !block.is(BlockTags.WITHER_IMMUNE);
    }

    @Override
    public void handleEntityEvent(byte status) {
        this.roaringAnimationState.stop();
        this.throwingAnimationState.stop();
        this.attackingAnimationState.stop();

        if (status == 4) {
            this.roaringAnimationState.stop();
            this.attackingAnimationState.start(this.tickCount);
        } else if (status == 62) {
            this.roaringAnimationState.stop();
            this.throwingAnimationState.start(this.tickCount);
        } else if (status == 5) {
            this.roaringAnimationState.stop();
            this.attackingAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(status);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (DATA_POSE.equals(data)) {
            switch (this.getPose()) {
                case ROARING, SNIFFING -> this.roaringAnimationState.start(this.tickCount);
                case STANDING -> this.roaringAnimationState.stop();
                default -> {}
            }
        }

        super.onSyncedDataUpdated(data);
    }

    @Override
    protected @NotNull Brain<?> makeBrain(Dynamic<?> dynamic) {
        return BloaterBrain.create(this, dynamic);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Brain<BloaterEntity> getBrain() {
        return (Brain<BloaterEntity>) super.getBrain();
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
        valueOutput.store("listener", Data.CODEC, this.vibrationData);
    }

    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.angerManagement = valueInput.read("anger", AngerManagement.codec(this::isValidTarget)).orElseGet(() -> new AngerManagement(this::isValidTarget, Collections.emptyList()));
        this.updateAnger();
        this.vibrationData = valueInput.read("listener", Data.CODEC).orElseGet(Data::new);
    }


    public BloaterAngriness getAngriness() {
        return BloaterAngriness.getForAnger(this.getAngerAtTarget());
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
            if (entity instanceof Player && bl && BloaterAngriness.getForAnger(i).isAngry()) {
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
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if( source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)){
            amount *= 1.5F;
        }

        boolean bl = super.hurtServer(world, source, amount);

        if (!this.isNoAi()) {
            Entity entity = source.getEntity();
            this.increaseAngerAt(entity, BloaterAngriness.ANGRY.getThreshold() + 20, false);

            if (entity != null) {
                double radius = CALLING_RADIUS;
                List<BloaterEntity> nearbyBloaters = world.getEntitiesOfClass(
                        BloaterEntity.class,
                        this.getBoundingBox().inflate(radius),
                        e -> e != this && e.isAlive() && !e.isNoAi()
                );

                List<ClickerEntity> nearbyClickers = world.getEntitiesOfClass(
                        ClickerEntity.class,
                        this.getBoundingBox().inflate(radius),
                        e -> e.isAlive() && !e.isNoAi() && e.getTarget() == null
                );

                for (BloaterEntity bloater : nearbyBloaters)
                    bloater.increaseAngerAt(entity, BloaterAngriness.ANGRY.getThreshold() + 10, false);

                for( ClickerEntity clicker : nearbyClickers)
                    clicker.increaseAngerAt(entity, ClickerAngriness.ANGRY.getThreshold() + 10, false);

            }
            if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity livingEntity) {
                if (source.isDirect() || this.closerThan(livingEntity, 5.0F)) {
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
            BloaterBrain.lookAtDisturbance(this, entity.blockPosition());
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
        private final PositionSource positionSource = new EntityPositionSource(BloaterEntity.this, BloaterEntity.this.getEyeHeight());

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
            if (!BloaterEntity.this.isNoAi() && !BloaterEntity.this.isDeadOrDying() && !BloaterEntity.this.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN) && world.getWorldBorder().isWithinBounds(pos)) {
                if (context != null) {
                    Entity sourceEntity = context.sourceEntity();
                    if (sourceEntity instanceof LivingEntity livingEntity) {
                        return BloaterEntity.this.isValidTarget(livingEntity);
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onReceiveVibration(ServerLevel world, BlockPos pos, Holder<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            if (!BloaterEntity.this.isDeadOrDying()) {
                BloaterEntity.this.brain.setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 20L);
                world.broadcastEntityEvent(BloaterEntity.this, (byte) 61);
                if (!BloaterEntity.this.hasPose(Pose.ROARING)) {
                    BloaterEntity.this.playSound(ModSounds.BLOATER_ALERT, 1.0F, BloaterEntity.this.getVoicePitch());
                }
                BlockPos blockPos = pos;
                if (entity != null) {
                    if (BloaterEntity.this.closerThan(entity, 30.0)) {
                        if (BloaterEntity.this.getBrain().hasMemoryValue(MemoryModuleType.RECENT_PROJECTILE)) {
                            if (BloaterEntity.this.isValidTarget(entity)) {
                                blockPos = entity.blockPosition();
                            }

                            BloaterEntity.this.increaseAngerAt(entity);
                        } else {
                            BloaterEntity.this.increaseAngerAt(entity, 10, true);
                        }
                    }

                    BloaterEntity.this.getBrain().setMemoryWithExpiry(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
                } else {
                    BloaterEntity.this.increaseAngerAt(sourceEntity);
                }

                if (!BloaterEntity.this.getAngriness().isAngry()) {
                    Optional<LivingEntity> optional = BloaterEntity.this.angerManagement.getActiveEntity();
                    if (entity != null || optional.isEmpty() || optional.get() == sourceEntity) {
                        BloaterBrain.lookAtDisturbance(BloaterEntity.this, blockPos);
                    }
                }
            }
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON) {
            return false;
        }

        return super.canBeAffected(effect);
    }
}
