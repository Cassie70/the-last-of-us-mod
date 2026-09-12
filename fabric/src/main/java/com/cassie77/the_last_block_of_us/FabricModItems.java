package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.item.MedkitItem;
import com.cassie77.the_last_block_of_us.item.bottle.BottleItem;
import com.cassie77.the_last_block_of_us.item.micotoxinsac.MycotoxinSacItem;
import com.cassie77.the_last_block_of_us.item.molotov.MolotovItem;
import com.cassie77.the_last_block_of_us.item.nailbomb.NailBombItem;
import com.cassie77.the_last_block_of_us.item.smokebomb.SmokeBombItem;
import com.cassie77.the_last_block_of_us.item.upgratedpipe.UpgratedPipeItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;


import java.util.function.Function;

public class FabricModItems {

    public static Item register(String name, Function<Item.Properties, Item> itemFactory) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
        Item item = itemFactory.apply(new Item.Properties().setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        Constants.LOG.info("Registering {} Items", Constants.MOD_ID);

        ModItems.ALCOHOL = register("alcohol", Item::new);
        ModItems.ALCOHOL_HALF = register("alcohol_half", Item::new);
        ModItems.ALCOHOL_QUARTER = register("alcohol_quarter", Item::new);

        ModItems.BINDING = register("binding", Item::new);
        ModItems.BINDING_HALF = register("binding_half", Item::new);
        ModItems.BINDING_QUARTER = register("binding_quarter", Item::new);

        ModItems.BLADE = register("blade", Item::new);
        ModItems.BLADE_HALF = register("blade_half", Item::new);
        ModItems.BLADE_QUARTER = register("blade_quarter", Item::new);

        ModItems.RAG = register("rag", Item::new);
        ModItems.RAG_HALF = register("rag_half", Item::new);
        ModItems.RAG_QUARTER = register("rag_quarter", Item::new);

        ModItems.CANISTER = register("canister", Item::new);

        ModItems.MOLOTOV = register("molotov", MolotovItem::new);
        ModItems.MEDKIT = register("medkit", MedkitItem::new);
        ModItems.BOTTLE = register("bottle", BottleItem::new);
        ModItems.NAIL_BOMB = register("nail_bomb", NailBombItem::new);
        ModItems.MYCOTOXIN_SAC = register("mycotoxin_sac", MycotoxinSacItem::new);
        ModItems.SMOKE_BOMB = register("smoke_bomb", SmokeBombItem::new);

        ModItems.PIPE = register("pipe", properties -> new Item(properties.sword(ToolMaterial.IRON, 2.0F, -2.0F).durability(8)));
        ModItems.UPGRADED_PIPE = register("upgraded_pipe", properties -> new UpgratedPipeItem(properties.sword(ToolMaterial.IRON, 27.0F, -3.5F).durability(3)));
        ModItems.SHIV = register("shiv", properties -> new Item(properties.sword(ToolMaterial.IRON, 17.0F, -2.5F).durability(3)));

        ModItems.CLICKER_SPAWN_EGG = register("clicker_spawn_egg", properties -> new SpawnEggItem(ModEntities.CLICKER, properties));
        ModItems.BLOATER_SPAWN_EGG = register("bloater_spawn_egg", properties -> new SpawnEggItem(ModEntities.BLOATER, properties));

    }
}
