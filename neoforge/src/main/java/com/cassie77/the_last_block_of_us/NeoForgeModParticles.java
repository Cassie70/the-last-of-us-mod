package com.cassie77.the_last_block_of_us;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgeModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Constants.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CORROSION_SMOKE =
            PARTICLES.register("corrosion_smoke", () -> {
                SimpleParticleType particle = new SimpleParticleType(false);
                ModParticles.CORROSION_SMOKE = particle;
                return particle;
            });

    private NeoForgeModParticles() {
    }
}
