package com.cassie77;

public class CommonClass {
    public static void init() {
        Constants.LOG.info("Initializing common mod components for {}", Constants.MOD_NAME);
        ModItems.initialize();
        ModSounds.initialize();
        ModEntities.initialize();
        ModSensors.initialize();
        ModBlocks.initialize();
    }
}
