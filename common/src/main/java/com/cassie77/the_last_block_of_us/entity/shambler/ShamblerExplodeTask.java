package com.cassie77.the_last_block_of_us.entity.shambler;

import com.cassie77.the_last_block_of_us.entity.infected.InfectedEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Fight behaviour for the Shambler: it walks towards its target, primes a short
 * fuse when close enough and then detonates. Survivors flee while the explosion
 * cooldown is active, then try again.
 */
public class ShamblerExplodeTask extends Behavior<InfectedEntity> {

    private static final double DETONATION_DISTANCE_SQR = 6.25;
    private static final int FUSE_TICKS = 20;

    private static final float APPROACH_SPEED = 1.5F;
    private static final float FLEE_SPEED = 1.5F;
    private static final int FLEE_RADIUS = 8;
    private static final int FLEE_VERTICAL_RANGE = 7;
    /** Once it is this far from the target it stops backing off and just waits out the cooldown. */
    private static final double FLEE_DISTANCE_SQR = 36.0;

    private int fuseTicks;
    private boolean fusing;

    public ShamblerExplodeTask() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull InfectedEntity entity) {
        return entity instanceof ShamblerEntity && !entity.isDeadOrDying();
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel level, @NotNull InfectedEntity entity, long gameTime) {
        return entity instanceof ShamblerEntity shambler
                && !shambler.isDeadOrDying()
                && shambler.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected void start(@NotNull ServerLevel level, @NotNull InfectedEntity entity, long gameTime) {
        this.fusing = false;
        this.fuseTicks = 0;
    }

    @Override
    protected void tick(@NotNull ServerLevel level, @NotNull InfectedEntity entity, long gameTime) {
        if (!(entity instanceof ShamblerEntity shambler)) {
            return;
        }

        LivingEntity target = shambler.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if (target == null || !target.isAlive() || !shambler.isValidTarget(target)) {
            this.cancelFuse(level, shambler);
            return;
        }

        if (shambler.isExplosionOnCooldown()) {
            this.flee(shambler, target);
            return;
        }

        if (this.fusing) {
            this.tickFuse(level, shambler, target);
            return;
        }

        if (shambler.distanceToSqr(target) <= DETONATION_DISTANCE_SQR) {
            this.beginFuse(shambler);
        } else {
            BehaviorUtils.setWalkAndLookTargetMemories(shambler, target, APPROACH_SPEED, 0);
        }
    }

    @Override
    protected void stop(@NotNull ServerLevel level, @NotNull InfectedEntity entity, long gameTime) {
        if (entity instanceof ShamblerEntity shambler) {
            this.cancelFuse(level, shambler);
        }

        this.fusing = false;
        this.fuseTicks = 0;
    }

    private void beginFuse(ShamblerEntity shambler) {
        this.fusing = true;
        this.fuseTicks = 0;
        shambler.getNavigation().stop();
        shambler.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        shambler.startExplosionFuse();
    }

    private void tickFuse(ServerLevel level, ShamblerEntity shambler, LivingEntity target) {
        shambler.getNavigation().stop();
        shambler.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        shambler.getLookControl().setLookAt(target, 30.0F, 30.0F);

        this.fuseTicks++;
        if (this.fuseTicks >= FUSE_TICKS) {
            this.fusing = false;
            this.fuseTicks = 0;
            shambler.detonate();
        }
    }

    private void flee(ShamblerEntity shambler, LivingEntity target) {
        if (shambler.distanceToSqr(target) >= FLEE_DISTANCE_SQR) {
            shambler.getNavigation().stop();
            shambler.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            return;
        }

        if (!shambler.getNavigation().isDone()) {
            return;
        }

        Vec3 away = LandRandomPos.getPosAway(shambler, FLEE_RADIUS, FLEE_VERTICAL_RANGE, target.position());
        if (away != null) {
            BehaviorUtils.setWalkAndLookTargetMemories(shambler,
                    BlockPos.containing(away.x, away.y, away.z), FLEE_SPEED, 0);
        } else {
            BehaviorUtils.setWalkAndLookTargetMemories(shambler, target, FLEE_SPEED, 0);
        }
    }

    private void cancelFuse(ServerLevel level, ShamblerEntity shambler) {
        if (!this.fusing) {
            return;
        }

        this.fusing = false;
        this.fuseTicks = 0;
        shambler.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        shambler.setPose(Pose.STANDING);
        level.broadcastEntityEvent(shambler, (byte) 6);
    }
}
