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

public class ModItems {

    public static final Item RAG = register("rag", Item::new);
    public static final Item BLADE = register("blade", Item::new);
    public static final Item ALCOHOL = register("alcohol", Item::new);
    public static final Item BINDING = register("binding", Item::new);
    public static final Item CANISTER = register("canister", Item::new);

    public static final Item MOLOTOV = register("molotov", MolotovItem::new);
    public static final Item MEDKIT = register("medkit", MedkitItem::new);
    public static final Item BOTTLE = register("bottle", BottleItem::new);
    public static final Item NAIL_BOMB = register("nail_bomb", NailBombItem::new);
    public static final Item MYCOTOXIN_SAC = register("mycotoxin_sac", MycotoxinSacItem::new);
    public static final Item SMOKE_BOMB = register("smoke_bomb", SmokeBombItem::new);

    public static final Item PIPE = register("pipe", properties -> new Item(properties.sword(ToolMaterial.IRON, 2.0F, -2.0F).durability(8)));
    public static final Item UPGRADED_PIPE = register("upgraded_pipe", properties -> new UpgratedPipeItem(properties.sword(ToolMaterial.IRON, 27.0F, -3.5F).durability(3)));
    public static final Item SHIV = register("shiv", properties -> new Item(properties.sword(ToolMaterial.IRON, 17.0F, -3.5F).durability(3)));

    public static final Item CLICKER_SPAWN_EGG = register("clicker_spawn_egg", properties -> new SpawnEggItem(ModEntities.CLICKER, properties));
    public static final Item BLOATER_SPAWN_EGG = register("bloater_spawn_egg", properties -> new SpawnEggItem(ModEntities.BLOATER, properties));

    public static Item register(String name, Function<Item.Properties, Item> itemFactory) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
        Item item = itemFactory.apply(new Item.Properties().setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        Constants.LOG.info("Registering {} Items", Constants.MOD_ID);
    }
}
