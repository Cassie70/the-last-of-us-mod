package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.effect.CorrosionEffect;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class FabricModEffects {

    private FabricModEffects() {
    }

    public static void initialize() {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "corrosion");
        ModEffects.CORROSION = Registry.register(BuiltInRegistries.MOB_EFFECT, location, new CorrosionEffect());
    }
}
