package com.cassie77.the_last_block_of_us;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static final RegistryObject<Item> RAG = registerSimpleItem("rag");
    public static final RegistryObject<Item> BLADE = registerSimpleItem("blade");
    public static final RegistryObject<Item> ALCOHOL = registerSimpleItem("alcohol");
    public static final RegistryObject<Item> BINDING = registerSimpleItem("binding");
    public static final RegistryObject<Item> CANISTER = registerSimpleItem("canister");
    public static final RegistryObject<Item> MOLOTOV = registerSimpleItem("molotov");
    public static final RegistryObject<Item> MEDKIT = registerSimpleItem("medkit");
    public static final RegistryObject<Item> BOTTLE = registerSimpleItem("bottle");
    public static final RegistryObject<Item> NAIL_BOMB = registerSimpleItem("nail_bomb");
    public static final RegistryObject<Item> MYCOTOXIN_SAC = registerSimpleItem("mycotoxin_sac");
    public static final RegistryObject<Item> SMOKE_BOMB = registerSimpleItem("smoke_bomb");
    public static final RegistryObject<Item> PIPE = registerSimpleItem("pipe");
    public static final RegistryObject<Item> UPGRADED_PIPE = registerSimpleItem("upgraded_pipe");
    public static final RegistryObject<Item> SHIV = registerSimpleItem("shiv");

    // Para ítems especiales como los Spawn Eggs, regístralos directamente creando la instancia:
    public static final RegistryObject<Item> CLICKER_SPAWN_EGG = ITEMS.register("clicker_spawn_egg", () -> {
        Item item = new Item(createProperties("clicker_spawn_egg")); // O la clase personalizada de tu Spawn Egg
        ModItems.CLICKER_SPAWN_EGG = item;
        return item;
    });

    public static final RegistryObject<Item> BLOATER_SPAWN_EGG = ITEMS.register("bloater_spawn_egg", () -> {
        Item item = new Item(createProperties("bloater_spawn_egg")); // O la clase personalizada de tu Spawn Egg
        ModItems.BLOATER_SPAWN_EGG = item;
        return item;
    });

    private static Item.Properties createProperties(String name) {
        return new Item.Properties().setId(
                ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name))
        );
    }

    private static RegistryObject<Item> registerSimpleItem(String name) {
        return ITEMS.register(name, () -> {
            Item item = new Item(createProperties(name));

            switch (name) {
                case "rag" -> ModItems.RAG = item;
                case "blade" -> ModItems.BLADE = item;
                case "alcohol" -> ModItems.ALCOHOL = item;
                case "binding" -> ModItems.BINDING = item;
                case "canister" -> ModItems.CANISTER = item;
                case "molotov" -> ModItems.MOLOTOV = item;
                case "medkit" -> ModItems.MEDKIT = item;
                case "bottle" -> ModItems.BOTTLE = item;
                case "nail_bomb" -> ModItems.NAIL_BOMB = item;
                case "mycotoxin_sac" -> ModItems.MYCOTOXIN_SAC = item;
                case "smoke_bomb" -> ModItems.SMOKE_BOMB = item;
                case "pipe" -> ModItems.PIPE = item;
                case "upgraded_pipe" -> ModItems.UPGRADED_PIPE = item;
                case "shiv" -> ModItems.SHIV = item;
            }
            return item;
        });
    }
}