package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.effect.CorrosionEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgeModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Constants.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> CORROSION = EFFECTS.register("corrosion", () -> {
        MobEffect effect = new CorrosionEffect();
        ModEffects.CORROSION = effect;
        return effect;
    });

    private NeoForgeModEffects() {
    }
}
