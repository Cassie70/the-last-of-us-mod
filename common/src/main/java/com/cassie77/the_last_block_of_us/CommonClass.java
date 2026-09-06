package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

public class CommonClass {

    public static void init() {
        Constants.LOG.info(
                "Hello from Common init on {}! we are currently in a {} environment!",
                Services.PLATFORM.getPlatformName(),
                Services.PLATFORM.getEnvironmentName()
        );

        ModItems.initialize();
        ModSounds.initialize();
        ModEntities.initialize();
        ModSensors.initialize();
        ModBlocks.initialize();
    }
}