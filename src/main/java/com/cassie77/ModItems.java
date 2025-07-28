package com.cassie77;

import com.cassie77.item.MedkitItem;
import com.cassie77.item.bottle.BottleItem;
import com.cassie77.item.micotoxinsac.MycotoxinSacItem;
import com.cassie77.item.molotov.MolotovItem;
import com.cassie77.item.nailbomb.NailBombItem;
import com.cassie77.item.upgratedpipe.UpgratedPipeItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;


import java.util.function.Function;

import static com.cassie77.TheLastOfUsMod.MOD_ID;

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

    public static final Item PIPE = register("pipe", settings -> new Item(settings.sword(ToolMaterial.IRON, 2f, -2f).maxDamage(8)));
    public static final Item UPGRADED_PIPE = register("upgraded_pipe", settings -> new UpgratedPipeItem(settings.sword(ToolMaterial.IRON, 27f, -3.5f).maxDamage(3)));
    public static final Item SHIV = register("shiv", settings -> new Item(settings.sword(ToolMaterial.IRON, 17f, -3.5f).maxDamage(3)));

    public static final Item CLICKER_SPAWN_EGG = register("clicker_spawn_egg", settings -> new SpawnEggItem(ModEntities.CLICKER, settings));

    public static Item register(String name, Function<Item.Settings, Item> itemFactory) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(new Item.Settings().registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(RAG);
            entries.add(BLADE);
            entries.add(ALCOHOL);
            entries.add(BINDING);
            entries.add(CANISTER);
        }
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(BOTTLE);
            entries.add(MOLOTOV);
            entries.add(NAIL_BOMB);
            entries.add(MEDKIT);
            entries.add(PIPE);
            entries.add(UPGRADED_PIPE);
            entries.add(SHIV);
            entries.add(MYCOTOXIN_SAC);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(CLICKER_SPAWN_EGG);
        });
    }

}