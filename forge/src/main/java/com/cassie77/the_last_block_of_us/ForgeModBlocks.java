package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.block.ClickerSpawnerBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ForgeModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static final RegistryObject<Block> CORDYCEPS_BLOCK = register(
            "cordyceps_block",
            BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "cordyceps_block"))),
            Block::new
    );

    public static final RegistryObject<Block> CLICKER_SPAWNER_BLOCK = register(
            "clicker_spawner_block",
            BlockBehaviour.Properties.of()
                    .noCollission()
                    .noOcclusion()
                    .instabreak()
                    .replaceable()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "clicker_spawner_block"))),
            ClickerSpawnerBlock::new
    );

    private ForgeModBlocks() {
    }

    private static RegistryObject<Block> register(String name, BlockBehaviour.Properties properties,
                                                  java.util.function.Function<BlockBehaviour.Properties, Block> factory) {

        RegistryObject<Block> block = BLOCKS.register(name, () -> {
            Block registeredBlock = factory.apply(properties);
            if (name.equals("cordyceps_block")) {
                ModBlocks.CORDYCEPS_BLOCK = registeredBlock;
            } else if (name.equals("clicker_spawner_block")) {
                ModBlocks.CLICKER_SPAWNER_BLOCK = registeredBlock;
            }
            return registeredBlock;
        });

        // Al crear las propiedades del Item, también debemos asignar su setId()
        ITEMS.register(name, () -> new BlockItem(
                block.get(),
                new Item.Properties()
                        .useBlockDescriptionPrefix()
                        .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name)))
        ));

        return block;
    }
}