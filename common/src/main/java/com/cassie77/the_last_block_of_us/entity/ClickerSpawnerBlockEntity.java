package com.cassie77.the_last_block_of_us.entity;

import com.cassie77.the_last_block_of_us.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ClickerSpawnerBlockEntity extends BlockEntity {

    public ClickerSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CLICKER_SPAWNER_BLOCK_ENTITY, pos, blockState);
    }
}