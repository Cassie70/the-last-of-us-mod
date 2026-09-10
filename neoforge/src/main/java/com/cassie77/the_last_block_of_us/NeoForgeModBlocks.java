package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.block.ClickerSpawnerBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Function;

public final class NeoForgeModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Constants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Constants.MOD_ID);

    public static final DeferredHolder<Block, Block> CORDYCEPS_BLOCK = register("cordyceps_block",
            BlockBehaviour.Properties.of().setId(key(Registries.BLOCK, "cordyceps_block")), Block::new);
    public static final DeferredHolder<Block, Block> CLICKER_SPAWNER_BLOCK = register("clicker_spawner_block",
            BlockBehaviour.Properties.of().noCollission().noOcclusion().instabreak().replaceable()
                    .setId(key(Registries.BLOCK, "clicker_spawner_block")),
            ClickerSpawnerBlock::new);

    private NeoForgeModBlocks() {
    }

    private static DeferredHolder<Block, Block> register(String name, BlockBehaviour.Properties properties,
            Function<BlockBehaviour.Properties, Block> factory) {
        DeferredHolder<Block, Block> block = BLOCKS.register(name, () -> {
            Block registeredBlock = factory.apply(properties);
            if (name.equals("cordyceps_block")) {
                ModBlocks.CORDYCEPS_BLOCK = registeredBlock;
            } else {
                ModBlocks.CLICKER_SPAWNER_BLOCK = registeredBlock;
            }
            return registeredBlock;
        });
        ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().useBlockDescriptionPrefix().setId(key(Registries.ITEM, name))));
        return block;
    }

    private static <T> ResourceKey<T> key(ResourceKey<? extends net.minecraft.core.Registry<T>> registry, String name) {
        return ResourceKey.create(registry, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }
}