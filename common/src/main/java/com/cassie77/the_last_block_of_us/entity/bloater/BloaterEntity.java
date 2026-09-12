package com.cassie77.the_last_block_of_us.entity.bloater;

import com.cassie77.the_last_block_of_us.ModSounds;
import com.cassie77.the_last_block_of_us.entity.infected.InfectedAngriness;
import com.cassie77.the_last_block_of_us.entity.infected.InfectedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BloaterEntity extends InfectedEntity {

    private static final double MAX_HEALTH = 300.0;
    private static final double MOVE_SPEED = 0.3;
    private static final double KNOCKBACK_RESISTANCE = 0.75;
    private static final double ATTACK_KNOCKBACK = 2.0;
    private static final double ATTACK_DAMAGE = 20.0;
    private static final double FOLLOW_RANGE = 16.0;
    private static final double WATER_MOVEMENT_EFFICIENCY = 0.8;
    private static final int WEAPON_DISABLE_BLOCKING_SECONDS = 5;
    private static final int BREAKING_BLOCK_COOLDOWN = 30;
    private int blockBreakingCooldown = 0;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState roaringAnimationState = new AnimationState();
    public final AnimationState throwingAnimationState = new AnimationState();

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
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, WATER_MOVEMENT_EFFICIENCY);
    }

    @Override
    protected BehaviorControl<? super InfectedEntity> getAdditionalFightTask() {
        return new ThrowTask();
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, @NotNull Entity target) {
        ThrowTask.cooldown(this, 40);
        return super.doHurtTarget(world, target);
    }

    @Override
    protected void customServerAiStep(@NotNull ServerLevel world) {
        super.customServerAiStep(world);

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
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON) {
            return false;
        }

        return super.canBeAffected(effect);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getPose() == Pose.STANDING
                && !this.idleAnimationState.isStarted()
                && !this.attackingAnimationState.isStarted()
                && !this.roaringAnimationState.isStarted()) {
            this.idleAnimationState.start(this.tickCount);
        }
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
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> data) {
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
    public SoundEvent getAmbientSound(InfectedAngriness angriness) {
        return switch (angriness) {
            case CALM -> ModSounds.BLOATER_AMBIENT;
            case AGITATED -> ModSounds.BLOATER_AMBIENT;
            case ANGRY -> ModSounds.BLOATER_AMBIENT;
        };
    }

    @Override
    public SoundEvent getListeningSound(InfectedAngriness angriness) {
        return switch (angriness) {
            case CALM -> ModSounds.BLOATER_ALERT;
            case AGITATED -> ModSounds.BLOATER_ALERT;
            case ANGRY -> ModSounds.BLOATER_ALERT;
        };
    }

    @Override
    public SoundEvent getSniffSound() {
        return ModSounds.BLOATER_AWARE;
    }

    @Override
    public SoundEvent getAngrySound() {
        return ModSounds.BLOATER_ANGRY;
    }

    @Override
    protected int getInfectedMeleeAttackInterval() { return 20; }

    @Override
    protected int getInfectedAngrinessAmount() { return 45; }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return ModSounds.BLOATER_ALERT;
    }
}
