package com.cassie77.the_last_block_of_us.entity.clicker;

import com.cassie77.the_last_block_of_us.ModSounds;
import com.cassie77.the_last_block_of_us.entity.infected.InfectedAngriness;
import com.cassie77.the_last_block_of_us.entity.infected.InfectedEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ClickerEntity extends InfectedEntity {
    private static final Predicate<Difficulty> DOOR_BREAK_DIFFICULTY_CHECKER = (difficulty) -> difficulty == Difficulty.HARD;
    private final ClickerBreakDoorGoal breakDoorsGoal;
    private boolean canBreakDoors;

    private static final double MAX_HEALTH = 40.0;
    private static final double MOVE_SPEED = 0.3;
    private static final double KNOCKBACK_RESISTANCE = 0.0;
    private static final double ATTACK_KNOCKBACK = 1.0;
    private static final double ATTACK_DAMAGE = 15.0;
    private static final double FOLLOW_RANGE = 8.0;
    private static final int WEAPON_DISABLE_BLOCKING_SECONDS = 3;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState roaringAnimationState = new AnimationState();

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
    public float getSecondsToDisableBlocking() {
        return WEAPON_DISABLE_BLOCKING_SECONDS;
    }

    private void setCanBreakDoors(boolean canBreakDoors) {
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
    protected void addAdditionalSaveData(@NotNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putBoolean("CanBreakDoors", this.canBreakDoors);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setCanBreakDoors(valueInput.getBooleanOr("CanBreakDoors", false));
    }


    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor world, @NotNull DifficultyInstance difficulty, @NotNull EntitySpawnReason spawnReason, SpawnGroupData entityData) {
        this.setCanBreakDoors(true);
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
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
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> data) {
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
    public SoundEvent getAmbientSound(InfectedAngriness angriness) {
        return switch (angriness) {
            case CALM -> ModSounds.CLICKER_AMBIENT;
            case AGITATED -> ModSounds.CLICKER_AMBIENT;
            case ANGRY -> ModSounds.CLICKER_ALERT_ANGRY;
        };
    }

    @Override
    public SoundEvent getListeningSound(InfectedAngriness angriness) {
        return switch (angriness) {
            case CALM -> ModSounds.CLICKER_ALERT;
            case AGITATED -> ModSounds.CLICKER_ALERT;
            case ANGRY -> ModSounds.CLICKER_ALERT_ANGRY;
        };
    }

    @Override
    public SoundEvent getSniffSound() {
        return ModSounds.CLICKER_AWARE;
    }

    @Override
    public SoundEvent getAngrySound() {
        return ModSounds.CLICKER_ANGRY;
    }



}
