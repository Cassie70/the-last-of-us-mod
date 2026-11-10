package com.cassie77.the_last_block_of_us.entity.shambler;

import com.cassie77.the_last_block_of_us.ModEffects;
import com.cassie77.the_last_block_of_us.entity.CustomAreaEffectCloudEntity;
import com.cassie77.the_last_block_of_us.entity.infected.InfectedAngriness;
import com.cassie77.the_last_block_of_us.entity.infected.InfectedEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;

/**
 * Infected that does not punch: it shambles towards its target, primes a short
 * fuse (creeper style) and detonates in a mycotoxin burst. It survives its own
 * explosion, flees for a while and then tries again.
 */
public class ShamblerEntity extends InfectedEntity {

    private static final double MAX_HEALTH = 80.0;
    private static final double MOVE_SPEED = 0.23;
    private static final double KNOCKBACK_RESISTANCE = 0.5;
    private static final double ATTACK_KNOCKBACK = 0;
    private static final double ATTACK_DAMAGE = 8.0;
    private static final double FOLLOW_RANGE = 16.0;
    private static final double WATER_MOVEMENT_EFFICIENCY = 0.8;

    /**
     * Custom entity event for the fuse. Avoid 21, 35 and 63: the client's
     * {@code ClientPacketListener} intercepts them (guardian, totem and sniffer)
     * before dispatching to {@code handleEntityEvent}.
     */
    public static final byte EVENT_EXPLOSION_FUSE = 64;

    /**
     * Vanilla couples reach and damage: entities are damaged out to {@code power * 2}
     * blocks, with damage falling off from the centre. 3.0F reaches ~6 blocks
     * (creeper-level), so stepping back a couple of blocks no longer avoids the blast.
     */
    private static final float EXPLOSION_POWER = 3.0F;
    private static final float CLOUD_RADIUS = 6.0F;
    private static final int CLOUD_DURATION = 300;
    private static final int CLOUD_CORROSION_DURATION = 40;
    private static final int CLOUD_CORROSION_AMPLIFIER = 3;
    private static final int CLOUD_SLOWNESS_DURATION = 200;
    private static final int CLOUD_SLOWNESS_AMPLIFIER = 1;
    private static final int EXPLOSION_COOLDOWN_TICKS = 40;

    /** Death animation tick (0..20) at which the pending death explosion triggers. */
    private static final int DEATH_EXPLOSION_TICK = 20;
    /** Vanilla removes a dead living entity at tick 20; with a pending explosion we wait one extra tick. */
    private static final int DEATH_REMOVAL_TICK = 21;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState roaringAnimationState = new AnimationState();

    private int explosionCooldown;
    private boolean deathExplosionPending;
    private boolean deathExplosionPlayed;

    public ShamblerEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);

        this.xpReward = 5;
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_OTHER, 8.0F);
        this.setPathfindingMalus(PathType.POWDER_SNOW, 8.0F);
        this.setPathfindingMalus(PathType.LAVA, 8.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
    }

    public static AttributeSupplier.Builder addAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVE_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
                .add(Attributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, WATER_MOVEMENT_EFFICIENCY);
    }

    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.SLOWNESS) {
            return false;
        }

        return super.canBeAffected(effect);
    }

    @Override
    protected boolean usesMeleeCombat() {
        return false;
    }

    @Override
    protected BehaviorControl<? super InfectedEntity> getAdditionalFightTask() {
        return new ShamblerExplodeTask();
    }

    public boolean isExplosionOnCooldown() {
        return this.explosionCooldown > 0;
    }

    /**
     * Called when the fuse starts: plays the creeper hiss and tells the client to
     * play the attack animation.
     */
    public void startExplosionFuse() {
        this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.0F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.broadcastEntityEvent(this, EVENT_EXPLOSION_FUSE);
        }
    }

    /**
     * Spawns the mycotoxin explosion and its lingering cloud, then starts the
     * cooldown so the mob flees instead of detonating again immediately.
     */
    public void detonate() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        this.explodeAndCloud(serverLevel);
        this.explosionCooldown = EXPLOSION_COOLDOWN_TICKS;
        this.setPose(Pose.STANDING);
        serverLevel.broadcastEntityEvent(this, (byte) 6);
    }

    /**
     * Only the explosion and the cloud, without touching the pose or the animation
     * state. Also used by the death sequence once the death animation has finished.
     */
    private void explodeAndCloud(ServerLevel serverLevel) {
        boolean wasInvulnerable = this.isInvulnerable();
        this.setInvulnerable(true);
        try {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);
            serverLevel.explode(this, this.getX(), this.getY(), this.getZ(),
                    EXPLOSION_POWER, Level.ExplosionInteraction.NONE);
        } finally {
            this.setInvulnerable(wasInvulnerable);
        }

        this.spawnExplosionParticles(serverLevel);
        this.spawnMycotoxinCloud(serverLevel);
    }

    /**
     * Explicit explosion burst: {@code ExplosionInteraction.NONE} does not always
     * produce a visible blast on the client, so the particles are sent directly.
     */
    private void spawnExplosionParticles(ServerLevel serverLevel) {
        double x = this.getX();
        double y = this.getY() + this.getBbHeight() * 0.5;
        double z = this.getZ();

        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 10, 1.5, 0.75, 1.5, 0.0);
        serverLevel.sendParticles(ParticleTypes.FIREFLY, x, y, z, 30, 2.0, 1.0, 2.0, 0.05);
    }

    private void spawnMycotoxinCloud(ServerLevel serverLevel) {
        CustomAreaEffectCloudEntity cloud = new CustomAreaEffectCloudEntity(serverLevel,
                this.getX(), this.getY(), this.getZ());
        cloud.setOwner(this);
        cloud.setParticleArea(ParticleTypes.FIREFLY);
        cloud.setDensityFactor(0.05F);
        cloud.setRadius(CLOUD_RADIUS);
        // No radiusOnUse: a hazard cloud should keep its size instead of shrinking by 0.5
        // every time it affects an entity (that made the cloud much smaller than it looked).
        cloud.setDuration(CLOUD_DURATION);
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        cloud.addEffect(new MobEffectInstance(ModEffects.corrosion(), CLOUD_CORROSION_DURATION, CLOUD_CORROSION_AMPLIFIER));
        cloud.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, CLOUD_SLOWNESS_DURATION, CLOUD_SLOWNESS_AMPLIFIER));
        serverLevel.addFreshEntity(cloud);
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if (!this.level().isClientSide()) {
            this.deathExplosionPending = true;
        }

        super.die(source);
    }

    /**
     * Replaces the vanilla death tick so the corpse survives for the whole death
     * animation (20 ticks) and detonates on its final frame.
     */
    @Override
    protected void tickDeath() {
        this.deathTime++;

        if (this.level().isClientSide()) {
            return;
        }

        if (this.deathExplosionPending && !this.deathExplosionPlayed
                && this.deathTime >= DEATH_EXPLOSION_TICK) {
            this.deathExplosionPlayed = true;
            if (this.level() instanceof ServerLevel serverLevel) {
                this.explodeAndCloud(serverLevel);
            }
        }

        int removalTick = this.deathExplosionPending ? DEATH_REMOVAL_TICK : 20;
        if (this.deathTime >= removalTick && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            if (this.explosionCooldown > 0) {
                this.explosionCooldown--;
            }
            return;
        }

        if (this.getPose() == Pose.STANDING) {
            if (!this.idleAnimationState.isStarted()
                    && !this.attackingAnimationState.isStarted()
                    && !this.roaringAnimationState.isStarted()) {
                this.idleAnimationState.start(this.tickCount);
            }
        } else {
            this.idleAnimationState.stop();
        }
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 4 || status == 5 || status == EVENT_EXPLOSION_FUSE) {
            this.roaringAnimationState.stop();
            this.idleAnimationState.stop();
            this.attackingAnimationState.start(this.tickCount);
        } else if (status == 6) {
            this.attackingAnimationState.stop();
            if (this.getPose() == Pose.STANDING && !this.idleAnimationState.isStarted()) {
                this.idleAnimationState.start(this.tickCount);
            }
        } else {
            super.handleEntityEvent(status);
        }
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> data) {
        if (DATA_POSE.equals(data)) {
            switch (this.getPose()) {
                case ROARING, SNIFFING -> {
                    this.idleAnimationState.stop();

                    if (!this.roaringAnimationState.isStarted()) {
                        this.roaringAnimationState.start(this.tickCount);
                    }
                }
                case STANDING -> {
                    this.roaringAnimationState.stop();
                    this.idleAnimationState.start(this.tickCount);
                }
                default -> {
                }
            }
        }

        super.onSyncedDataUpdated(data);
    }

    @Override
    public SoundEvent getAmbientSound(InfectedAngriness angriness) {
        return switch (angriness) {
            case CALM, AGITATED -> SoundEvents.ZOMBIE_AMBIENT;
            case ANGRY -> SoundEvents.ZOMBIE_DEATH;
        };
    }

    @Override
    public SoundEvent getListeningSound(InfectedAngriness angriness) {
        return switch (angriness) {
            case CALM, AGITATED -> SoundEvents.ZOMBIE_AMBIENT;
            case ANGRY -> SoundEvents.ZOMBIE_DEATH;
        };
    }

    @Override
    public SoundEvent getSniffSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    @Override
    public SoundEvent getAngrySound() {
        return SoundEvents.ZOMBIE_DEATH;
    }
}
