package com.cassie77.entity.clicker;

import com.cassie77.ModEntities;
import com.cassie77.ModSounds;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ClickerEntity extends HostileEntity implements Vibrations {

    private static final TrackedData<Integer> ANGER;
    private static final double MAX_HEALTH = 40.0;
    private static final double MOVE_SPEED = 0.3;
    private static final double KNOCKBACK_RESISTANCE = 0.0;
    private static final double ATTACK_KNOCKBACK = 1.0;
    private static final double ATTACK_DAMAGE = 20.0;
    private static final double FOLLOW_RANGE = 24.0;
    private static final int ANGRINESS_AMOUNT = 35;
    private static final int WEAPON_DISABLE_BLOCKING_SECONDS = 3;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState roaringAnimationState = new AnimationState();

    private final EntityGameEventHandler<Vibrations.VibrationListener> gameEventHandler = new EntityGameEventHandler<>(new Vibrations.VibrationListener(this));
    private final Vibrations.Callback vibrationCallback = new ClickerEntity.VibrationCallback();
    private Vibrations.ListenerData vibrationListenerData = new Vibrations.ListenerData();
    WardenAngerManager angerManager = new WardenAngerManager(this::isValidTarget, Collections.emptyList());

    public ClickerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);

        this.experiencePoints = 20;
        this.getNavigation().setCanSwim(true);
        this.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0F);
        this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);

    }

    public boolean canSpawn(WorldView world) {
        return super.canSpawn(world) && world.isSpaceEmpty(this, this.getType().getDimensions().getBoxAt(this.getPos()));
    }

    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0F;
    }

    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    public float getWeaponDisableBlockingForSeconds() {
        return WEAPON_DISABLE_BLOCKING_SECONDS;
    }

    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.55F;
    }

    public static DefaultAttributeContainer.Builder addAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, MAX_HEALTH).add(EntityAttributes.MOVEMENT_SPEED, MOVE_SPEED).add(EntityAttributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE).add(EntityAttributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK).add(EntityAttributes.ATTACK_DAMAGE, ATTACK_DAMAGE).add(EntityAttributes.FOLLOW_RANGE, FOLLOW_RANGE);
    }

    public boolean occludeVibrationSignals() {
        return true;
    }

    protected float getSoundVolume() {
        return 2.0F;
    }

    @Override
    public void playAmbientSound() {
        super.playSound(this.getAngriness().getSound(), 2.0F, 1.0F);
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    public boolean tryAttack(ServerWorld world, Entity target) {
        world.sendEntityStatus(this, (byte)4);
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1.5F, this.getSoundPitch());
        return super.tryAttack(world, target);
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ANGER, 0);
    }

    private void updateAnger() {
        this.dataTracker.set(ANGER, this.getAngerAtTarget());
    }

    public void tick() {
        World var2 = this.getWorld();
        if (var2 instanceof ServerWorld serverWorld) {
            Ticker.tick(serverWorld, this.vibrationListenerData, this.vibrationCallback);
        }

        if (this.getPose() == EntityPose.STANDING
                && !this.idleAnimationState.isRunning()
                && !this.attackingAnimationState.isRunning()
                && !this.roaringAnimationState.isRunning()) {
            this.idleAnimationState.start(this.age);
        }

        super.tick();
    }

    protected void mobTick(ServerWorld world) {
        Profiler profiler = Profilers.get();
        profiler.push("ClickerBrain");
        this.getBrain().tick(world, this);
        profiler.pop();
        super.mobTick(world);

        if (this.age % 20 == 0) {
            this.angerManager.tick(world, this::isValidTarget);
            this.updateAnger();
        }

        ClickerBrain.updateActivities(this);
    }

    public void handleStatus(byte status) {
        if (status == 4) {
            this.roaringAnimationState.stop();
            this.attackingAnimationState.start(this.age);
        }  else {
            super.handleStatus(status);
        }

    }
    
    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            switch (this.getPose()) {
                case ROARING -> this.roaringAnimationState.start(this.age);
                case SNIFFING -> {
                    this.roaringAnimationState.stop();
                    this.roaringAnimationState.start(this.age);
                }
                case STANDING -> {
                    this.roaringAnimationState.stop();
                    this.attackingAnimationState.stop();
                    this.idleAnimationState.start(this.age);
                }

            }
        }

        super.onTrackedDataSet(data);
    }
    
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return ClickerBrain.create(this, dynamic);
    }

    @SuppressWarnings("unchecked")
    public Brain<ClickerEntity> getBrain() {
        return (Brain<ClickerEntity>) super.getBrain();
    }

    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
        World var3 = this.getWorld();
        if (var3 instanceof ServerWorld serverWorld) {
            callback.accept(this.gameEventHandler, serverWorld);
        }

    }

    @Contract("null->false")
    public boolean isValidTarget(@Nullable Entity entity) {

        if (entity instanceof LivingEntity livingEntity) {
            return this.getWorld() == entity.getWorld() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity) && !this.isTeammate(entity) && livingEntity.getType() != EntityType.ARMOR_STAND && livingEntity.getType() != ModEntities.CLICKER && !livingEntity.isInvulnerable() && !livingEntity.isDead() && this.getWorld().getWorldBorder().contains(livingEntity.getBoundingBox());
        }
        return false;
    }
    
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.put("anger", WardenAngerManager.createCodec(this::isValidTarget), this.angerManager);
        view.put("listener", ListenerData.CODEC, this.vibrationListenerData);
    }

    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.angerManager = view.read("anger", WardenAngerManager.createCodec(this::isValidTarget)).orElseGet(() -> new WardenAngerManager(this::isValidTarget, Collections.emptyList()));
        this.updateAnger();
        this.vibrationListenerData = view.read("listener", ListenerData.CODEC).orElseGet(ListenerData::new);
    }

    private void playListeningSound() {
        if (!this.isInPose(EntityPose.ROARING)) {
            //this.playSound(this.getAngriness().getListeningSound(), 2.0F, this.getSoundPitch());
        }
    }

    public ClickerAngriness getAngriness() {
        return ClickerAngriness.getForAnger(this.getAngerAtTarget());
    }

    private int getAngerAtTarget() {
        return this.angerManager.getAngerFor(this.getTarget());
    }

    public void removeSuspect(Entity entity) {
        this.angerManager.removeSuspect(entity);
    }

    public void increaseAngerAt(@Nullable Entity entity) {
        this.increaseAngerAt(entity, ANGRINESS_AMOUNT, true);
    }

    @VisibleForTesting
    public void increaseAngerAt(@Nullable Entity entity, int amount, boolean listening) {
        if (!this.isAiDisabled() && this.isValidTarget(entity)) {
            boolean bl = !(this.getTarget() instanceof PlayerEntity);
            int i = this.angerManager.increaseAngerAt(entity, amount);
            if (entity instanceof PlayerEntity && bl && ClickerAngriness.getForAnger(i).isAngry()) {
                this.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
            }

            if (listening) {
                this.playListeningSound();
            }
        }

    }

    public Optional<LivingEntity> getPrimeSuspect() {
        return this.getAngriness().isAngry() ? this.angerManager.getPrimeSuspect() : Optional.empty();
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.getTargetInBrain();
    }

    
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        boolean bl = super.damage(world, source, amount);
        if (!this.isAiDisabled()) {
            Entity entity = source.getAttacker();
            this.increaseAngerAt(entity, ClickerAngriness.ANGRY.getThreshold() + 20, false);
            if (this.brain.getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity livingEntity) {
                if (source.isDirect() || this.isInRange(livingEntity, 5.0F)) {
                    this.updateAttackTarget(livingEntity);
                }
            }
        }

        return bl;
    }

    public void updateAttackTarget(LivingEntity target) {
        this.getBrain().forget(MemoryModuleType.ROAR_TARGET);
        this.getBrain().remember(MemoryModuleType.ATTACK_TARGET, target);
        this.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    public EntityDimensions getBaseDimensions(EntityPose pose) {
        return super.getBaseDimensions(pose);
    }

    public boolean isPushable() {
        return super.isPushable();
    }

    protected void pushAway(Entity entity) {
        if (!this.isAiDisabled() && !this.getBrain().hasMemoryModule(MemoryModuleType.TOUCH_COOLDOWN)) {
            this.getBrain().remember(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
            this.increaseAngerAt(entity);
            ClickerBrain.lookAtDisturbance(this, entity.getBlockPos());
        }

        super.pushAway(entity);
    }

    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world) {
            protected PathNodeNavigator createPathNodeNavigator(int range) {
                this.nodeMaker = new LandPathNodeMaker();
                return new PathNodeNavigator(this.nodeMaker, range) {
                    protected float getDistance(PathNode a, PathNode b) {
                        return a.getHorizontalDistance(b);
                    }
                };
            }
        };
    }

    public Vibrations.ListenerData getVibrationListenerData() {
        return this.vibrationListenerData;
    }

    public Vibrations.Callback getVibrationCallback() {
        return this.vibrationCallback;
    }

    static {
        ANGER = DataTracker.registerData(ClickerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    class VibrationCallback implements Vibrations.Callback {
        private static final int RANGE = 16;
        private final PositionSource positionSource = new EntityPositionSource(ClickerEntity.this, ClickerEntity.this.getStandingEyeHeight());

        VibrationCallback() {
        }

        public int getRange() {
            return RANGE;
        }

        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        public TagKey<GameEvent> getTag() {
            return GameEventTags.WARDEN_CAN_LISTEN;
        }

        public boolean triggersAvoidCriterion() {
            return true;
        }

        public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter) {
            if (!ClickerEntity.this.isAiDisabled() && !ClickerEntity.this.isDead() && !ClickerEntity.this.getBrain().hasMemoryModule(MemoryModuleType.VIBRATION_COOLDOWN) && world.getWorldBorder().contains(pos)) {
                Entity var6 = emitter.sourceEntity();

                if (var6 instanceof LivingEntity livingEntity) {
                    return ClickerEntity.this.isValidTarget(livingEntity);
                }
                return true;
            } else {
                return false;
            }
        }

        public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            if (!ClickerEntity.this.isDead()) {
                ClickerEntity.this.brain.remember(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
                world.sendEntityStatus(ClickerEntity.this, (byte)61);
                if (!ClickerEntity.this.isInPose(EntityPose.ROARING)) {
                    ClickerEntity.this.playSound(ModSounds.CLICKER_ALERT, 2.0F, ClickerEntity.this.getSoundPitch());
                }
                BlockPos blockPos = pos;
                if (entity != null) {
                    if (ClickerEntity.this.isInRange(entity, 30.0F)) {
                        if (ClickerEntity.this.getBrain().hasMemoryModule(MemoryModuleType.RECENT_PROJECTILE)) {
                            if (ClickerEntity.this.isValidTarget(entity)) {
                                blockPos = entity.getBlockPos();
                            }

                            ClickerEntity.this.increaseAngerAt(entity);
                        } else {
                            ClickerEntity.this.increaseAngerAt(entity, 10, true);
                        }
                    }

                    ClickerEntity.this.getBrain().remember(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
                } else {
                    ClickerEntity.this.increaseAngerAt(sourceEntity);
                }

                if (!ClickerEntity.this.getAngriness().isAngry()) {
                    Optional<LivingEntity> optional = ClickerEntity.this.angerManager.getPrimeSuspect();
                    if (entity != null || optional.isEmpty() || optional.get() == sourceEntity) {
                        ClickerBrain.lookAtDisturbance(ClickerEntity.this, blockPos);
                    }
                }

            }
        }
    }
}
