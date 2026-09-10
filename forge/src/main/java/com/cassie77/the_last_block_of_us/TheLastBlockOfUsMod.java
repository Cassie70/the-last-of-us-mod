package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.entity.bloater.BloaterEntity;
import com.cassie77.the_last_block_of_us.entity.clicker.ClickerEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.client.event.EntityRenderersEvent;

@Mod(Constants.MOD_ID)
public class TheLastBlockOfUsMod {

    public TheLastBlockOfUsMod(FMLJavaModLoadingContext context) {
        Constants.LOG.info("Initializing {} on Forge!", Constants.MOD_NAME);

        var modBusGroup = context.getModBusGroup();
        ForgeModBlocks.BLOCKS.register(modBusGroup);
        ForgeModBlocks.ITEMS.register(modBusGroup);
        ForgeModEntities.ENTITY_TYPES.register(modBusGroup);
        ForgeModSensors.SENSOR_TYPES.register(modBusGroup);
        ForgeModSounds.SOUNDS.register(modBusGroup);
        ForgeModBlockEntities.BLOCK_ENTITY_TYPES.register(modBusGroup);
        ForgeModItems.ITEMS.register(modBusGroup);
        CommonClass.init();

        EntityAttributeCreationEvent.getBus(modBusGroup).addListener(this::onEntityAttributeCreation);
        BuildCreativeModeTabContentsEvent.getBus(modBusGroup).addListener(this::onBuildCreativeTabs);
        SpawnPlacementRegisterEvent.getBus(modBusGroup).addListener(this::onRegisterSpawnPlacements);

        if (FMLLoader.getDist() == Dist.CLIENT) {
            EntityRenderersEvent.RegisterLayerDefinitions.getBus(modBusGroup)
                    .addListener(ForgeModClient::registerLayerDefinitions);
            EntityRenderersEvent.RegisterRenderers.getBus(modBusGroup)
                    .addListener(ForgeModClient::registerRenderers);
        }

    }

    private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CLICKER, ClickerEntity.addAttributes().build());
        event.put(ModEntities.BLOATER, BloaterEntity.addAttributes().build());
    }

    private void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.RAG);
            event.accept(ModItems.BLADE);
            event.accept(ModItems.ALCOHOL);
            event.accept(ModItems.ALCOHOL_HALF);
            event.accept(ModItems.ALCOHOL_QUARTER);
            event.accept(ModItems.BINDING);
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

    private void onRegisterSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(
                ModEntities.CLICKER,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);

        event.register(
                ModEntities.BLOATER,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}