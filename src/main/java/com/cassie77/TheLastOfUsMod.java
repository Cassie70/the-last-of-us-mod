package com.cassie77;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheLastOfUsMod implements ModInitializer {
	public static final String MOD_ID = "the-last-of-us-mod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModItems.initialize();
		ModSounds.initialize();
		ModEntities.initialize();
		ModSensors.initialize();
		ModBlocks.initialize();

		ModEntities.registerAttributes();
	}
}