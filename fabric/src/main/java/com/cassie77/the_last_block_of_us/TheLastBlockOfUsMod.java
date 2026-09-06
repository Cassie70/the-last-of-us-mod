package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
import com.cassie77.the_last_block_of_us.entity.clicker.ClickerEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;

public class TheLastBlockOfUsMod implements ModInitializer {
    
    @Override
    public void onInitialize() {

        Constants.LOG.info("Initializing {} on Fabric!", Constants.MOD_NAME);
        CommonClass.init();

        // Register default entity attributes
        FabricDefaultAttributeRegistry.register(ModEntities.CLICKER, ClickerEntity.addAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.BLOATER, BloaterEntity.addAttributes());

        // Register creative tab entries
        registerCreativeTabs();

        // Register entity spawns
        registerSpawns();
    }

    private void registerCreativeTabs() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.accept(ModItems.RAG);
            entries.accept(ModItems.BLADE);
            entries.accept(ModItems.ALCOHOL);
            entries.accept(ModItems.BINDING);
            entries.accept(ModItems.CANISTER);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(ModItems.BOTTLE);
            entries.accept(ModItems.MOLOTOV);
            entries.accept(ModItems.NAIL_BOMB);
            entries.accept(ModItems.SMOKE_BOMB);
            entries.accept(ModItems.MEDKIT);
            entries.accept(ModItems.PIPE);
            entries.accept(ModItems.UPGRADED_PIPE);
            entries.accept(ModItems.SHIV);
            entries.accept(ModItems.MYCOTOXIN_SAC);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
            entries.accept(ModBlocks.CORDYCEPS_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.accept(ModItems.CLICKER_SPAWN_EGG);
            entries.accept(ModItems.BLOATER_SPAWN_EGG);
        });
    }

    private void registerSpawns() {
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                MobCategory.MONSTER, ModEntities.CLICKER, 5, 1, 4);

        SpawnPlacements.register(ModEntities.CLICKER,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                MobCategory.MONSTER, ModEntities.BLOATER, 2, 1, 1);

        SpawnPlacements.register(ModEntities.BLOATER,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);
    }
}
