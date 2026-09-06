package com.cassie77.entity.bloater;

import com.cassie77.ModItems;
import com.cassie77.item.micotoxinsac.MycotoxinSacEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ThrowTask extends Behavior<BloaterEntity> {
    private static final int HORIZONTAL_RANGE = 15;
    private static final int VERTICAL_RANGE = 20;
    public static final int COOLDOWN = 40;
    private static final int EXTENDED_COOLDOWN = 200;
    private static final int MAX_THROWS = 3;
    private static final int SOUND_DELAY = Mth.ceil(34.0);
    private static final int RUN_TIME = Mth.ceil(60.0);
    private static final float THROW_SPEED = 2.0F;
    private static final float THROW_PITCH = 0.0F;
    private static final int ITEM_APPEAR_DELAY = 20;

    private int ticksSinceStart = 0;
    private int throwsCount = 0;

    public ThrowTask() {
        super(
                ImmutableMap.of(
                        MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryStatus.REGISTERED,
                        MemoryModuleType.SONIC_BOOM_SOUND_DELAY, MemoryStatus.REGISTERED
                ),
                RUN_TIME
        );
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverWorld, BloaterEntity bloaterEntity) {
        return bloaterEntity.closerThan(
                bloaterEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(),
                HORIZONTAL_RANGE,
                VERTICAL_RANGE
        );
    }

    @Override
    protected boolean canStillUse(ServerLevel serverWorld, BloaterEntity bloaterEntity, long l) {
        return throwsCount < MAX_THROWS;
    }

    @Override
    protected void start(ServerLevel serverWorld, BloaterEntity bloaterEntity, long l) {
        bloaterEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, RUN_TIME);
        bloaterEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_DELAY, Unit.INSTANCE, SOUND_DELAY);
        serverWorld.broadcastEntityEvent(bloaterEntity, (byte) 62);

        ticksSinceStart = 0;
        throwsCount = 0;
    }

    @Override
    protected void tick(ServerLevel serverWorld, BloaterEntity bloaterEntity, long l) {
        ticksSinceStart++;

        if (ticksSinceStart == ITEM_APPEAR_DELAY) {
            bloaterEntity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ModItems.MYCOTOXIN_SAC));
        }

        bloaterEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                .ifPresent((target) -> bloaterEntity.getLookControl().setLookAt(target.position()));

        if (!bloaterEntity.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_DELAY) &&
                !bloaterEntity.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN)) {

            bloaterEntity.getBrain().setMemoryWithExpiry(
                    MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN,
                    Unit.INSTANCE,
                    (RUN_TIME - SOUND_DELAY)
            );

            bloaterEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                    .filter(bloaterEntity::isValidTarget)
                    .filter((target) -> bloaterEntity.closerThan(target, HORIZONTAL_RANGE, VERTICAL_RANGE))
                    .ifPresent((target) -> {
                        MycotoxinSacEntity mycotoxinSacEntity = new MycotoxinSacEntity(serverWorld, bloaterEntity, bloaterEntity.getItemInHand(InteractionHand.OFF_HAND));
                        mycotoxinSacEntity.setPos(bloaterEntity.getX(), bloaterEntity.getEyeY() - 0.1, bloaterEntity.getZ());

                        Vec3 targetPos = target.getEyePosition().subtract(mycotoxinSacEntity.position());
                        mycotoxinSacEntity.shoot(targetPos.x, targetPos.y, targetPos.z, THROW_SPEED, THROW_PITCH);

                        serverWorld.addFreshEntity(mycotoxinSacEntity);

                        bloaterEntity.playSound(SoundEvents.SNOWBALL_THROW, 3.0F, 1.0F);
                        bloaterEntity.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);

                        throwsCount++;
                    });
        }
    }

    @Override
    protected void stop(ServerLevel serverWorld, BloaterEntity bloaterEntity, long l) {
        ticksSinceStart = 0;

        if (throwsCount >= MAX_THROWS) {
            cooldown(bloaterEntity, EXTENDED_COOLDOWN);
            throwsCount = 0;
        } else {
            cooldown(bloaterEntity, COOLDOWN);
        }
    }

    public static void cooldown(LivingEntity bloater, int cooldown) {
        bloater.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_COOLDOWN, Unit.INSTANCE, cooldown);
    }
}
