package com.cassie77;

import com.cassie77.entity.clicker.ClickerEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheLastOfUsMod implements ModInitializer {
	public static final String MOD_ID = "the-last-of-us-mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		// The mod ID is used to identify your mod in various places, such as the config file.
		LOGGER.info("Hello Fabric world!");
		ModItems.initialize();
		ModSounds.initialize();
		ModEntities.initialize();
		ModSensors.initialize();
		ModBlocks.initialize();

		ModEntities.registerAttributes();
	}
}