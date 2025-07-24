package com.cassie77;

import com.cassie77.entity.bloater.BloaterEntity;
import com.cassie77.entity.clicker.ClickerEntity;
import com.cassie77.item.bottle.BottleEntity;
import com.cassie77.item.molotov.MolotovEntity;
import com.cassie77.item.nailbomb.NailBombEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static com.cassie77.TheLastOfUsMod.MOD_ID;

public class ModEntities {

    public static final EntityType<MolotovEntity> MOLOTOV_ENTITY = register("molotov_entity", EntityType.Builder.<MolotovEntity>create(MolotovEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<BottleEntity> BOTTLE_ENTITY = register("bottle_entity", EntityType.Builder.<BottleEntity>create(BottleEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<NailBombEntity> NAIL_BOMB_ENTITY = register("nail_bomb_entity", EntityType.Builder.<NailBombEntity>create(NailBombEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));

    public static final EntityType<ClickerEntity> CLICKER = register("clicker", EntityType.Builder.create(ClickerEntity::new, SpawnGroup.MONSTER)
            .dimensions(0.6F, 1.95F).eyeHeight(1.74F).passengerAttachments(2.0125F).vehicleAttachment(-0.7F).maxTrackingRange(8));

    public static final EntityType<BloaterEntity> BLOATER = register("bloater", EntityType.Builder.create(BloaterEntity::new, SpawnGroup.MONSTER)
            .dimensions(1.0F, 2.25F).eyeHeight(1.8F).passengerAttachments(3.0F).vehicleAttachment(-1.0F).maxTrackingRange(32));


    private static <T extends Entity> EntityType<T> register(RegistryKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

    public static void initialize() {
        // Initialization logic if needed
    }

    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(CLICKER, ClickerEntity.addAttributes());
        FabricDefaultAttributeRegistry.register(BLOATER, BloaterEntity.addAttributes());
    }
}
