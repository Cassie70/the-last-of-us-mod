package com.cassie77;

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

    public static final Item RAG = register("rag", Item::new, new Item.Settings());
    public static final Item BLADE = register("blade", Item::new, new Item.Settings());
    public static final Item ALCOHOL = register("alcohol", Item::new, new Item.Settings());
    public static final Item BINDING = register("binding", Item::new, new Item.Settings());

    public static final Item MOLOTOV = register("molotov", MolotovItem::new, new Item.Settings());
    public static final Item MEDKIT = register("medkit", Item::new, new Item.Settings());
    public static final Item BOTTLE = register("bottle", SnowballItem::new, new Item.Settings());
    public static final Item NAIL_BOMB = register("nail_bomb", SnowballItem::new, new Item.Settings());

    public static final Item PIPE = register("pipe", settings -> new Item(settings.sword(ToolMaterial.IRON, 2f, -3f).maxDamage(8)), new Item.Settings());
    public static final Item SHIV = register("shiv", settings -> new Item(settings.sword(ToolMaterial.IRON, 17f, -2.4f).maxDamage(3)), new Item.Settings());

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

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
        }
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(BOTTLE);
            entries.add(MOLOTOV);
            entries.add(NAIL_BOMB);
            entries.add(MEDKIT);
            entries.add(PIPE);
            entries.add(SHIV);
        });
    }

}