package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.block.ClickerSpawnerBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class FabricModBlocks {

    private FabricModBlocks() {
    }

    public static void initialize() {
        ModBlocks.CORDYCEPS_BLOCK = register("cordyceps_block", BlockBehaviour.Properties.of());
        ModBlocks.CLICKER_SPAWNER_BLOCK = register("clicker_spawner_block", BlockBehaviour.Properties.of()
                .noCollission()
                .noOcclusion()
                .instabreak()
                .replaceable(), ClickerSpawnerBlock::new);
    }

    private static Block register(String name, BlockBehaviour.Properties properties) {
        return register(name, properties, Block::new);
    }

    private static Block register(String name, BlockBehaviour.Properties properties,
                                  java.util.function.Function<BlockBehaviour.Properties, Block> factory) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
        Block block = factory.apply(properties.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
        return block;
    }
}
