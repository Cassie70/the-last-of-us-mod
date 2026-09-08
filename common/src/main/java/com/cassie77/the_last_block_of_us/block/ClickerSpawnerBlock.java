package com.cassie77.the_last_block_of_us.block;

import com.cassie77.the_last_block_of_us.ModEntities;
import com.cassie77.the_last_block_of_us.entity.ClickerSpawnerBlockEntity;
import com.cassie77.the_last_block_of_us.entity.clicker.ClickerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClickerSpawnerBlock extends Block implements EntityBlock {

    private static final double DETECTION_RADIUS = 16.0; // 16 bloques cubre una habitación completa de forma más segura durante worldgen
    private static final float SPAWN_CHANCE = 0.5f;

    public ClickerSpawnerBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ClickerSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, blockEntity) -> {
            if (lvl instanceof ServerLevel serverLevel) {
                tick(serverLevel, pos, serverLevel.getRandom());
            }
        };
    }

    private void tick(ServerLevel level, BlockPos pos, RandomSource random) {
        Player nearestPlayer = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), DETECTION_RADIUS, false);

        if (nearestPlayer != null && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(nearestPlayer)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

            if (random.nextFloat() < SPAWN_CHANCE) {
                ClickerEntity clicker = ModEntities.CLICKER.create(level, EntitySpawnReason.TRIGGERED);
                if (clicker != null) {
                    clicker.snapTo(
                            pos.getX() + 0.5,
                            pos.getY(),
                            pos.getZ() + 0.5,
                            random.nextFloat() * 360.0F,
                            0.0F
                    );
                    clicker.setPersistenceRequired();
                    level.addFreshEntityWithPassengers(clicker);
                }
            }
        }
    }
}