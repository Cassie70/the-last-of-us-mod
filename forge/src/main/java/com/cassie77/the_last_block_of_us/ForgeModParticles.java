package com.cassie77.the_last_block_of_us;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ForgeModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Constants.MOD_ID);

    public static final RegistryObject<ParticleType<?>> CORROSION_SMOKE =
            PARTICLES.register("corrosion_smoke", () -> {
                SimpleParticleType particle = new SimpleParticleType(false);
                ModParticles.CORROSION_SMOKE = particle;
                return particle;
            });

    private ForgeModParticles() {
    }
}
