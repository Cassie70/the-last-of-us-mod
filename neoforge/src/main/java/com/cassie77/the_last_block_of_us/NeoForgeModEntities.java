package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.CustomAreaEffectCloudEntity;
import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
import com.cassie77.the_last_block_of_us.entity.clicker.ClickerEntity;
import com.cassie77.the_last_block_of_us.item.bottle.BottleEntity;
import com.cassie77.the_last_block_of_us.item.micotoxinsac.MycotoxinSacEntity;
import com.cassie77.the_last_block_of_us.item.molotov.MolotovEntity;
import com.cassie77.the_last_block_of_us.item.nailbomb.NailBombEntity;
import com.cassie77.the_last_block_of_us.item.smokebomb.SmokeBombEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Consumer;

public final class NeoForgeModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE,
            Constants.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<MolotovEntity>> MOLOTOV_ENTITY = register(
            "molotov_entity", EntityType.Builder.<MolotovEntity>of(MolotovEntity::new, MobCategory.MISC).noLootTable()
                    .sized(.25F, .25F).clientTrackingRange(4).updateInterval(10),
            entity -> ModEntities.MOLOTOV_ENTITY = entity);
    public static final DeferredHolder<EntityType<?>, EntityType<BottleEntity>> BOTTLE_ENTITY = register(
            "bottle_entity", EntityType.Builder.<BottleEntity>of(BottleEntity::new, MobCategory.MISC).noLootTable()
                    .sized(.25F, .25F).clientTrackingRange(4).updateInterval(10),
            entity -> ModEntities.BOTTLE_ENTITY = entity);
    public static final DeferredHolder<EntityType<?>, EntityType<NailBombEntity>> NAIL_BOMB_ENTITY = register(
            "nail_bomb_entity", EntityType.Builder.<NailBombEntity>of(NailBombEntity::new, MobCategory.MISC)
                    .noLootTable().sized(.25F, .25F).clientTrackingRange(4).updateInterval(10),
            entity -> ModEntities.NAIL_BOMB_ENTITY = entity);
    public static final DeferredHolder<EntityType<?>, EntityType<SmokeBombEntity>> SMOKE_BOMB_ENTITY = register(
            "smoke_bomb_entity",
            EntityType.Builder.<SmokeBombEntity>of(SmokeBombEntity::new, MobCategory.MISC).noLootTable()
                    .sized(.25F, .25F).clientTrackingRange(4).updateInterval(10),
            entity -> ModEntities.SMOKE_BOMB_ENTITY = entity);
    public static final DeferredHolder<EntityType<?>, EntityType<MycotoxinSacEntity>> MYCOTOXIN_SAC_ENTITY = register(
            "mycotoxin_sac_entity",
            EntityType.Builder.<MycotoxinSacEntity>of(MycotoxinSacEntity::new, MobCategory.MISC).noLootTable()
                    .sized(.25F, .25F).clientTrackingRange(4).updateInterval(10),
            entity -> ModEntities.MYCOTOXIN_SAC_ENTITY = entity);
    public static final DeferredHolder<EntityType<?>, EntityType<ClickerEntity>> CLICKER = register("clicker",
            EntityType.Builder.of(ClickerEntity::new, MobCategory.MONSTER).sized(.6F, 1.95F)
                    .passengerAttachments(2.0125F).clientTrackingRange(8),
            entity -> ModEntities.CLICKER = entity);
    public static final DeferredHolder<EntityType<?>, EntityType<BloaterEntity>> BLOATER = register("bloater",
            EntityType.Builder.of(BloaterEntity::new, MobCategory.MONSTER).sized(.9F, 2.25F).passengerAttachments(3.15F)
                    .clientTrackingRange(16),
            entity -> ModEntities.BLOATER = entity);
    public static final DeferredHolder<EntityType<?>, EntityType<CustomAreaEffectCloudEntity>> CUSTOM_AREA_EFFECT_CLOUD_ENTITY = register(
            "area_effect_cloud",
            EntityType.Builder.<CustomAreaEffectCloudEntity>of(CustomAreaEffectCloudEntity::new, MobCategory.MISC)
                    .noLootTable().fireImmune().sized(6.0F, 3.0F).clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE),
            entity -> ModEntities.CUSTOM_AREA_EFFECT_CLOUD_ENTITY = entity);

    private NeoForgeModEntities() {
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name,
            EntityType.Builder<T> builder, Consumer<EntityType<T>> setter) {
        return ENTITY_TYPES.register(name, () -> {
            EntityType<T> entity = builder.build(ResourceKey.create(Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name)));
            setter.accept(entity);
            return entity;
        });
    }
}