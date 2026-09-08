package com.cassie77.the_last_block_of_us;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import com.cassie77.the_last_block_of_us.entity.ClickerSpawnerBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class FabricModBlockEntities {

    public static void register() {
        ModBlockEntities.CLICKER_SPAWNER_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "clicker_spawner_block_entity"),
                FabricBlockEntityTypeBuilder.create(
                        ClickerSpawnerBlockEntity::new,
                        ModBlocks.CLICKER_SPAWNER_BLOCK
                ).build()
        );
    }
}