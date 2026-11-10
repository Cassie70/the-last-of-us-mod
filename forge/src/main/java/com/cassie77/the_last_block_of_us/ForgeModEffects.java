package com.cassie77.the_last_block_of_us;

import com.cassie77.the_last_block_of_us.effect.CorrosionEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ForgeModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Constants.MOD_ID);

    public static final RegistryObject<MobEffect> CORROSION = EFFECTS.register("corrosion", () -> {
        MobEffect effect = new CorrosionEffect();
        ModEffects.CORROSION = effect;
        return effect;
    });

    private ForgeModEffects() {
    }
}
