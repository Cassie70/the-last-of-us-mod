package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.ClickerSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public final class ForgeModBlockEntities {

        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
                        .create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);

        public static final RegistryObject<BlockEntityType<ClickerSpawnerBlockEntity>> CLICKER_SPAWNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
                        .register("clicker_spawner_block_entity", () -> {
                                BlockEntityType<ClickerSpawnerBlockEntity> type = new BlockEntityType<>(
                                                ClickerSpawnerBlockEntity::new,
                                                Set.of(ForgeModBlocks.CLICKER_SPAWNER_BLOCK.get()));
                                ModBlockEntities.CLICKER_SPAWNER_BLOCK_ENTITY = type;
                                return type;
                        });

        private ForgeModBlockEntities() {
        }
}
