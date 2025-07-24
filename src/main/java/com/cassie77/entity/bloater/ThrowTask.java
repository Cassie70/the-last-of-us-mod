package com.cassie77.entity.bloater;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Optional;

public class ThrowTask extends MultiTickTask<BloaterEntity> {
    private static final int HORIZONTAL_RANGE = 15;
    private static final int VERTICAL_RANGE = 20;
    private static final double field_38852 = (double)0.5F;
    private static final double field_38853 = (double)2.5F;
    public static final int COOLDOWN = 40;
    private static final int SOUND_DELAY = MathHelper.ceil((double)34.0F);
    private static final int RUN_TIME = MathHelper.ceil(60.0F);

    public ThrowTask() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryModuleState.REGISTERED, MemoryModuleType.SONIC_BOOM_SOUND_DELAY, MemoryModuleState.REGISTERED), RUN_TIME);
    }

    protected boolean shouldRun(ServerWorld serverWorld, BloaterEntity bloaterEntity) {
        return bloaterEntity.isInRange((Entity)bloaterEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).get(), (double)15.0F, (double)20.0F);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, BloaterEntity bloaterEntity, long l) {
        return true;
    }

    protected void run(ServerWorld serverWorld, BloaterEntity bloaterEntity, long l) {
        bloaterEntity.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)RUN_TIME);
        bloaterEntity.getBrain().remember(MemoryModuleType.SONIC_BOOM_SOUND_DELAY, Unit.INSTANCE, (long)SOUND_DELAY);
        serverWorld.sendEntityStatus(bloaterEntity, (byte)62);
        bloaterEntity.playSound(SoundEvents.ENTITY_WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
    }

    protected void keepRunning(ServerWorld serverWorld, BloaterEntity bloaterEntity, long l) {
        bloaterEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((target) -> bloaterEntity.getLookControl().lookAt(target.getPos()));
        if (!bloaterEntity.getBrain().hasMemoryModule(MemoryModuleType.SONIC_BOOM_SOUND_DELAY) && !bloaterEntity.getBrain().hasMemoryModule(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN)) {
            bloaterEntity.getBrain().remember(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE, (long)(RUN_TIME - SOUND_DELAY));
            Optional<LivingEntity> var10000 = bloaterEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET);
            Objects.requireNonNull(bloaterEntity);
            var10000.filter(bloaterEntity::isValidTarget).filter((target) -> bloaterEntity.isInRange(target, (double)15.0F, (double)20.0F)).ifPresent((target) -> {
                Vec3d vec3d = bloaterEntity.getPos().add(bloaterEntity.getAttachments().getPoint(EntityAttachmentType.WARDEN_CHEST, 0, bloaterEntity.getYaw()));
                Vec3d vec3d2 = target.getEyePos().subtract(vec3d);
                Vec3d vec3d3 = vec3d2.normalize();
                int i = MathHelper.floor(vec3d2.length()) + 7;

                for(int j = 1; j < i; ++j) {
                    Vec3d vec3d4 = vec3d.add(vec3d3.multiply((double)j));
                    serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
                }

                bloaterEntity.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 3.0F, 1.0F);
                if (target.damage(serverWorld, serverWorld.getDamageSources().sonicBoom(bloaterEntity), 10.0F)) {
                    double d = (double)0.5F * ((double)1.0F - target.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE));
                    double e = (double)2.5F * ((double)1.0F - target.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE));
                    target.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d, vec3d3.getZ() * e);
                }

            });
        }
    }

    protected void finishRunning(ServerWorld serverWorld, BloaterEntity bloaterEntity, long l) {
        cooldown(bloaterEntity, 40);
    }

    public static void cooldown(LivingEntity warden, int cooldown) {
        warden.getBrain().remember(MemoryModuleType.SONIC_BOOM_COOLDOWN, Unit.INSTANCE, (long)cooldown);
    }
}
