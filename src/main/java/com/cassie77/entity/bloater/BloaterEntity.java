package com.cassie77.entity.bloater;

import com.cassie77.ModEntities;
import com.cassie77.ModSounds;
import com.cassie77.entity.clicker.ClickerAngriness;
import com.cassie77.entity.clicker.ClickerEntity;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.WaterAnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.*;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class BloaterEntity extends HostileEntity implements Vibrations {

    private static final TrackedData<Integer> ANGER;
    private static final double MAX_HEALTH = 300.0;
    private static final double MOVE_SPEED = 0.3;
    private static final double KNOCKBACK_RESISTANCE = 0.75;
    private static final double ATTACK_KNOCKBACK = 2;
    private static final double ATTACK_DAMAGE = 20.0;
    private static final double FOLLOW_RANGE = 6;
    private static final int ANGRINESS_AMOUNT = 45;
    private static final int WEAPON_DISABLE_BLOCKING_SECONDS = 5;
    private static final double CALLING_RADIUS = 3.0;
    private static final int BREAKING_BLOCK_COOLDOWN = 30;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState roaringAnimationState = new AnimationState();
    public final AnimationState throwingAnimationState = new AnimationState();

    private final EntityGameEventHandler<VibrationListener> gameEventHandler = new EntityGameEventHandler<>(new VibrationListener(this));
    private final Callback vibrationCallback = new VibrationCallback();
    private ListenerData vibrationListenerData = new ListenerData();
    WardenAngerManager angerManager = new WardenAngerManager(this::isValidTarget, Collections.emptyList());

    private int blockBreakingCooldown=0;

    public BloaterEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);

        this.experiencePoints = 200;
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
        ThrowTask.cooldown(this, 40);
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
        profiler.push("BloaterBrain");
        this.getBrain().tick(world, this);
        profiler.pop();
        super.mobTick(world);

        if (this.age % 20 == 0) {
            this.angerManager.tick(world, this::isValidTarget);
            this.updateAnger();
        }

        if (this.blockBreakingCooldown <= 0) {
            this.blockBreakingCooldown = BREAKING_BLOCK_COOLDOWN;
        }

        if(this.getAngriness().isAngry()) {
            if (this.blockBreakingCooldown > 0) {
                --this.blockBreakingCooldown;
                if (this.blockBreakingCooldown == 0 && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    boolean destroyed = false;
                    int j = MathHelper.floor(this.getWidth() / 2.0F + 1.0F);
                    int k = MathHelper.floor(this.getHeight());

                    for (BlockPos blockPos : BlockPos.iterate(this.getBlockX() - j, this.getBlockY(), this.getBlockZ() - j, this.getBlockX() + j, this.getBlockY() + k, this.getBlockZ() + j)) {
                        BlockState blockState = world.getBlockState(blockPos);
                        if (canDestroy(blockState)) {
                            destroyed = world.breakBlock(blockPos, true, this) || destroyed;
                        }
                    }

                    if (destroyed) {
                        world.syncWorldEvent(null, 1022, this.getBlockPos(), 0);
                        this.getWorld().sendEntityStatus(this, (byte)5);
                    }
                }
            }


        }
        BloaterBrain.updateActivities(this);
    }

    private static final Set<Block> BLOATER_IMMUNE = Set.of(
                Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS, Blocks.ENCHANTING_TABLE, Blocks.BEACON, Blocks.ENDER_CHEST
    );

    public static boolean canDestroy(BlockState block) {
        if(block.isIn(BlockTags.FLOWERS) || block.isIn(BlockTags.SMALL_FLOWERS)) return  false;

        if(BLOATER_IMMUNE.contains(block.getBlock())) return false;

        return !block.isAir() && !block.isIn(BlockTags.WITHER_IMMUNE);
    }

    @Override
    public void handleStatus(byte status) {
        this.roaringAnimationState.stop();
        this.throwingAnimationState.stop();
        this.attackingAnimationState.stop();

        if (status == 4) {
            this.roaringAnimationState.stop();
            this.attackingAnimationState.start(this.age);
        } else if (status == 62) {
            this.roaringAnimationState.stop();
            this.throwingAnimationState.start(this.age);
        } else if (status == 5) {
            this.roaringAnimationState.stop();
            this.attackingAnimationState.start(this.age);
        } else {
            super.handleStatus(status);
        }
    }
    
    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            switch (this.getPose()) {
                case ROARING, SNIFFING -> this.roaringAnimationState.start(this.age);
                case STANDING -> this.roaringAnimationState.stop();
            }
        }
        super.onTrackedDataSet(data);
    }
    
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return BloaterBrain.create(this, dynamic);
    }

    @SuppressWarnings("unchecked")
    public Brain<BloaterEntity> getBrain() {
        return (Brain<BloaterEntity>) super.getBrain();
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

    private static final Set<EntityType<?>> INVALID_TARGET_TYPES = Set.of(
            EntityType.ARMOR_STAND,
            ModEntities.CLICKER,
            ModEntities.BLOATER
    );


    @Contract("null->false")
    public boolean isValidTarget(@Nullable Entity entity) {

        if (entity instanceof LivingEntity livingEntity) {

            if (livingEntity instanceof WaterCreatureEntity || livingEntity instanceof WaterAnimalEntity) {
                return false;
            }
            return this.getWorld() == entity.getWorld() &&
                    EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity) &&
                    !this.isTeammate(entity) &&
                    !INVALID_TARGET_TYPES.contains(livingEntity.getType()) &&
                    !livingEntity.isInvulnerable() &&
                    !livingEntity.isDead() && this.getWorld().getWorldBorder().contains(livingEntity.getBoundingBox());
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

    public BloaterAngriness getAngriness() {
        return BloaterAngriness.getForAnger(this.getAngerAtTarget());
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
            if (entity instanceof PlayerEntity && bl && BloaterAngriness.getForAnger(i).isAngry()) {
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

        if( source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE)){
           amount *= 1.5F;
        }

        boolean bl = super.damage(world, source, amount);

        if (!this.isAiDisabled()) {
            Entity entity = source.getAttacker();
            this.increaseAngerAt(entity, BloaterAngriness.ANGRY.getThreshold() + 20, false);

            if (entity != null) {
                double radius = CALLING_RADIUS;
                List<BloaterEntity> nearbyBloaters = world.getEntitiesByClass(
                        BloaterEntity.class,
                        this.getBoundingBox().expand(radius),
                        e -> e != this && e.isAlive() && !e.isAiDisabled()
                );

                List<ClickerEntity> nearbyClickers = world.getEntitiesByClass(
                        ClickerEntity.class,
                        this.getBoundingBox().expand(radius),
                        e -> e.isAlive() && !e.isAiDisabled() && e.getTarget() == null
                );

                for (BloaterEntity bloater : nearbyBloaters)
                    bloater.increaseAngerAt(entity, BloaterAngriness.ANGRY.getThreshold() + 10, false);

                for( ClickerEntity clicker : nearbyClickers)
                    clicker.increaseAngerAt(entity, ClickerAngriness.ANGRY.getThreshold() + 10, false);

            }
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
        ThrowTask.cooldown(this, 200);
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
            BloaterBrain.lookAtDisturbance(this, entity.getBlockPos());
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


    public ListenerData getVibrationListenerData() {
        return this.vibrationListenerData;
    }

    public Callback getVibrationCallback() {
        return this.vibrationCallback;
    }

    static {
        ANGER = DataTracker.registerData(BloaterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    class VibrationCallback implements Callback {
        private static final int RANGE = 12;
        private final PositionSource positionSource = new EntityPositionSource(BloaterEntity.this, BloaterEntity.this.getStandingEyeHeight());

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
            if (!BloaterEntity.this.isAiDisabled() && !BloaterEntity.this.isDead() && !BloaterEntity.this.getBrain().hasMemoryModule(MemoryModuleType.VIBRATION_COOLDOWN) && world.getWorldBorder().contains(pos)) {
                Entity var6 = emitter.sourceEntity();

                if (var6 instanceof LivingEntity livingEntity) {
                    return BloaterEntity.this.isValidTarget(livingEntity);
                }
                return true;
            } else {
                return false;
            }
        }

        public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            if (!BloaterEntity.this.isDead()) {
                BloaterEntity.this.brain.remember(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
                world.sendEntityStatus(BloaterEntity.this, (byte)61);
                if (!BloaterEntity.this.isInPose(EntityPose.ROARING)) {
                    BloaterEntity.this.playSound(ModSounds.BLOATER_ALERT, 2.0F, BloaterEntity.this.getSoundPitch());
                }
                BlockPos blockPos = pos;
                if (entity != null) {
                    if (BloaterEntity.this.isInRange(entity, 30.0F)) {
                        if (BloaterEntity.this.getBrain().hasMemoryModule(MemoryModuleType.RECENT_PROJECTILE)) {
                            if (BloaterEntity.this.isValidTarget(entity)) {
                                blockPos = entity.getBlockPos();
                            }

                            BloaterEntity.this.increaseAngerAt(entity);
                        } else {
                            BloaterEntity.this.increaseAngerAt(entity, 10, true);
                        }
                    }

                    BloaterEntity.this.getBrain().remember(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
                } else {
                    BloaterEntity.this.increaseAngerAt(sourceEntity);
                }

                if (!BloaterEntity.this.getAngriness().isAngry()) {
                    Optional<LivingEntity> optional = BloaterEntity.this.angerManager.getPrimeSuspect();
                    if (entity != null || optional.isEmpty() || optional.get() == sourceEntity) {
                        BloaterBrain.lookAtDisturbance(BloaterEntity.this, blockPos);
                    }
                }

            }
        }
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
        if (effect.getEffectType() == StatusEffects.POISON) {
            return false;
        }
        return super.addStatusEffect(effect, source);
    }
}
