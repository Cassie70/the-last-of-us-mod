package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
import com.cassie77.the_last_block_of_us.entity.clicker.ClickerEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@Mod(Constants.MOD_ID)
public class TheLastBlockOfUsMod {

    public TheLastBlockOfUsMod(IEventBus eventBus) {
        Constants.LOG.info("Initializing {} on NeoForge!", Constants.MOD_NAME);

        NeoForgeModBlocks.BLOCKS.register(eventBus);
        NeoForgeModBlocks.ITEMS.register(eventBus);
        NeoForgeModEntities.ENTITY_TYPES.register(eventBus);
        NeoForgeModSensors.SENSOR_TYPES.register(eventBus);
        NeoForgeModSounds.SOUNDS.register(eventBus);
        NeoForgeModBlockEntities.BLOCK_ENTITY_TYPES.register(eventBus);
        NeoForgeModItems.ITEMS.register(eventBus);
        CommonClass.init();

        eventBus.addListener(this::onEntityAttributeCreation);
        eventBus.addListener(this::onBuildCreativeTabs);
        eventBus.addListener(this::onRegisterSpawnPlacements);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            eventBus.addListener(NeoForgeModClient::registerLayerDefinitions);
            eventBus.addListener(NeoForgeModClient::registerRenderers);
        }
    }

    private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CLICKER, ClickerEntity.addAttributes().build());
        event.put(ModEntities.BLOATER, BloaterEntity.addAttributes().build());
    }

    private void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.ALCOHOL);
            event.accept(ModItems.ALCOHOL_HALF);
            event.accept(ModItems.ALCOHOL_QUARTER);

            event.accept(ModItems.BINDING);
            event.accept(ModItems.BINDING_HALF);
            event.accept(ModItems.BINDING_QUARTER);

            event.accept(ModItems.BLADE);
            event.accept(ModItems.BLADE_HALF);
            event.accept(ModItems.BLADE_QUARTER);

            event.accept(ModItems.RAG);
            event.accept(ModItems.RAG_HALF);
            event.accept(ModItems.RAG_QUARTER);

            event.accept(ModItems.CANISTER);
        } else if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.BOTTLE);
            event.accept(ModItems.MOLOTOV);
            event.accept(ModItems.NAIL_BOMB);
            event.accept(ModItems.SMOKE_BOMB);
            event.accept(ModItems.MEDKIT);
            event.accept(ModItems.PIPE);
            event.accept(ModItems.UPGRADED_PIPE);
            event.accept(ModItems.SHIV);
            event.accept(ModItems.MYCOTOXIN_SAC);
        } else if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.CORDYCEPS_BLOCK);
        } else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.CLICKER_SPAWN_EGG);
            event.accept(ModItems.BLOATER_SPAWN_EGG);
        }
    }

    private void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntities.CLICKER,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkAnyLightMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(
                ModEntities.BLOATER,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkAnyLightMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

    }
}