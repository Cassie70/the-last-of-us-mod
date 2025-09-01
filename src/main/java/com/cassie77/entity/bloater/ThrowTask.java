package com.cassie77.entity.bloater;

import com.cassie77.ModItems;
import com.cassie77.item.micotoxinsac.MycotoxinSacEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Optional;

public class ThrowTask extends MultiTickTask<BloaterEntity> {
    private static final int HORIZONTAL_RANGE = 15;
    private static final int VERTICAL_RANGE = 20;
    public static final int COOLDOWN = 40;
    private static final int EXTENDED_COOLDOWN = 200;
    private static final int MAX_THROWS = 3;
    private static final int SOUND_DELAY = MathHelper.ceil(34.0);
    private static final int RUN_TIME = MathHelper.ceil(60.0);
    private static final float THROW_SPEED = 2F;
    private static final float THROW_PITCH = 0.0F;
    private static final int ITEM_APPEAR_DELAY = 20;

    private int ticksSinceStart = 0;
    private int throwsCount = 0;

    public ThrowTask() {
        super(
                ImmutableMap.of(
                        MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT,
                        MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryModuleState.VALUE_ABSENT,
                        MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryModuleState.REGISTERED,
                        MemoryModuleType.SONIC_BOOM_SOUND_DELAY, MemoryModuleState.REGISTERED
                ),
                RUN_TIME
        );
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, BloaterEntity bloaterEntity) {
        return bloaterEntity.isInRange(
                bloaterEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).get(),
                HORIZONTAL_RANGE,
                VERTICAL_RANGE
        );
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, BloaterEntity bloaterEntity, long l) {
        return throwsCount < MAX_THROWS;
    }

    @Override
    protected void run(ServerWorld serverWorld, BloaterEntity bloaterEntity, long l) {
        bloaterEntity.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, RUN_TIME);
        bloaterEntity.getBrain().remember(MemoryModuleType.SONIC_BOOM_SOUND_DELAY, Unit.INSTANCE, SOUND_DELAY);
        serverWorld.sendEntityStatus(bloaterEntity, (byte)62);

        ticksSinceStart = 0;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, BloaterEntity bloaterEntity, long l) {
        ticksSinceStart++;

        if (ticksSinceStart == ITEM_APPEAR_DELAY) {
            bloaterEntity.setStackInHand(Hand.OFF_HAND, new ItemStack(ModItems.MYCOTOXIN_SAC));
        }

        bloaterEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET)
                .ifPresent((target) -> bloaterEntity.getLookControl().lookAt(target.getPos()));

        if (!bloaterEntity.getBrain().hasMemoryModule(MemoryModuleType.SONIC_BOOM_SOUND_DELAY) &&
                !bloaterEntity.getBrain().hasMemoryModule(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN)) {

            bloaterEntity.getBrain().remember(
                    MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN,
                    Unit.INSTANCE,
                    (RUN_TIME - SOUND_DELAY)
            );

            bloaterEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET)
                    .filter(bloaterEntity::isValidTarget)
                    .filter((target) -> bloaterEntity.isInRange(target, HORIZONTAL_RANGE, VERTICAL_RANGE))
                    .ifPresent((target) -> {
                        // Crear y lanzar
                        MycotoxinSacEntity mycotoxinSacEntity = new MycotoxinSacEntity(serverWorld, bloaterEntity, bloaterEntity.getStackInHand(Hand.OFF_HAND));
                        mycotoxinSacEntity.setPosition(bloaterEntity.getX(), bloaterEntity.getEyeY() - 0.1, bloaterEntity.getZ());

                        Vec3d targetPos = target.getEyePos().subtract(mycotoxinSacEntity.getPos());
                        mycotoxinSacEntity.setVelocity(targetPos.x, targetPos.y, targetPos.z, THROW_SPEED, THROW_PITCH);

                        serverWorld.spawnEntity(mycotoxinSacEntity);

                        bloaterEntity.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 3.0F, 1.0F);
                        bloaterEntity.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);

                        throwsCount++;
                    });
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, BloaterEntity bloaterEntity, long l) {
        ticksSinceStart = 0;

        if (throwsCount >= MAX_THROWS) {
            cooldown(bloaterEntity, EXTENDED_COOLDOWN);
            throwsCount = 0;
        } else {
            cooldown(bloaterEntity, COOLDOWN);
        }
    }

    public static void cooldown(LivingEntity bloater, int cooldown) {
        bloater.getBrain().remember(MemoryModuleType.SONIC_BOOM_COOLDOWN, Unit.INSTANCE, cooldown);
    }
}
