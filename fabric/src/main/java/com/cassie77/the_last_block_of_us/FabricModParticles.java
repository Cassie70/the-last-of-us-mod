package com.cassie77.the_last_block_of_us;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class FabricModParticles {

    private FabricModParticles() {
    }

    public static void initialize() {
        ModParticles.CORROSION_SMOKE = Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                new ResourceLocation(
                        Constants.MOD_ID,
                        "corrosion_smoke"
                ),
                FabricParticleTypes.simple(false)
        );
    }
}
