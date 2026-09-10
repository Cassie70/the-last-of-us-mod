package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.item.MedkitItem;
import com.cassie77.the_last_block_of_us.item.bottle.BottleItem;
import com.cassie77.the_last_block_of_us.item.micotoxinsac.MycotoxinSacItem;
import com.cassie77.the_last_block_of_us.item.molotov.MolotovItem;
import com.cassie77.the_last_block_of_us.item.nailbomb.NailBombItem;
import com.cassie77.the_last_block_of_us.item.smokebomb.SmokeBombItem;
import com.cassie77.the_last_block_of_us.item.upgratedpipe.UpgratedPipeItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Function;

public final class ForgeModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static final RegistryObject<Item> RAG = register("rag", Item::new, item -> ModItems.RAG = item);
    public static final RegistryObject<Item> BLADE = register("blade", Item::new, item -> ModItems.BLADE = item);
    public static final RegistryObject<Item> ALCOHOL = register("alcohol", Item::new, item -> ModItems.ALCOHOL = item);
    public static final RegistryObject<Item> BINDING = register("binding", Item::new, item -> ModItems.BINDING = item);
    public static final RegistryObject<Item> CANISTER = register("canister", Item::new,
            item -> ModItems.CANISTER = item);

    public static final RegistryObject<Item> MOLOTOV = register("molotov", MolotovItem::new,
            item -> ModItems.MOLOTOV = item);
    public static final RegistryObject<Item> MEDKIT = register("medkit", MedkitItem::new,
            item -> ModItems.MEDKIT = item);
    public static final RegistryObject<Item> BOTTLE = register("bottle", BottleItem::new,
            item -> ModItems.BOTTLE = item);
    public static final RegistryObject<Item> NAIL_BOMB = register("nail_bomb", NailBombItem::new,
            item -> ModItems.NAIL_BOMB = item);
    public static final RegistryObject<Item> MYCOTOXIN_SAC = register("mycotoxin_sac", MycotoxinSacItem::new,
            item -> ModItems.MYCOTOXIN_SAC = item);
    public static final RegistryObject<Item> SMOKE_BOMB = register("smoke_bomb", SmokeBombItem::new,
            item -> ModItems.SMOKE_BOMB = item);

    public static final RegistryObject<Item> PIPE = register("pipe",
            properties -> new Item(properties.sword(ToolMaterial.IRON, 2.0F, -2.0F).durability(8)),
            item -> ModItems.PIPE = item);
    public static final RegistryObject<Item> UPGRADED_PIPE = register("upgraded_pipe",
            properties -> new UpgratedPipeItem(properties.sword(ToolMaterial.IRON, 27.0F, -3.5F).durability(3)),
            item -> ModItems.UPGRADED_PIPE = item);
    public static final RegistryObject<Item> SHIV = register("shiv",
            properties -> new Item(properties.sword(ToolMaterial.IRON, 17.0F, -3.5F).durability(3)),
            item -> ModItems.SHIV = item);

    public static final RegistryObject<Item> CLICKER_SPAWN_EGG = register("clicker_spawn_egg",
            properties -> new SpawnEggItem(ForgeModEntities.CLICKER.get(), properties),
            item -> ModItems.CLICKER_SPAWN_EGG = item);
    public static final RegistryObject<Item> BLOATER_SPAWN_EGG = register("bloater_spawn_egg",
            properties -> new SpawnEggItem(ForgeModEntities.BLOATER.get(), properties),
            item -> ModItems.BLOATER_SPAWN_EGG = item);

    private ForgeModItems() {
    }

    private static <T extends Item> RegistryObject<T> register(String name, Function<Item.Properties, T> factory,
            Consumer<T> setter) {
        return ITEMS.register(name, () -> {
            ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
            T item = factory.apply(new Item.Properties().setId(itemKey));
            setter.accept(item);
            return item;
        });
    }
}