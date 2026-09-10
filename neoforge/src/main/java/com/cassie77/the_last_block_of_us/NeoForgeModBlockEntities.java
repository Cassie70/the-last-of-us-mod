package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.ClickerSpawnerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

public final class NeoForgeModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClickerSpawnerBlockEntity>> CLICKER_SPAWNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
            .register(
                    "clicker_spawner_block_entity", () -> {
                        BlockEntityType<ClickerSpawnerBlockEntity> type = new BlockEntityType<>(
                                ClickerSpawnerBlockEntity::new, Set.of(NeoForgeModBlocks.CLICKER_SPAWNER_BLOCK.get()));
                        ModBlockEntities.CLICKER_SPAWNER_BLOCK_ENTITY = type;
                        return type;
                    });

    private NeoForgeModBlockEntities() {
    }
}