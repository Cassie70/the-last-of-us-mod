package com.cassie77.the_last_block_of_us;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public class ModEffects {

    /**
     * Assigned by the loader-specific registration class
     * ({@code FabricModEffects}, {@code ForgeModEffects}, {@code NeoForgeModEffects}).
     */
    public static MobEffect CORROSION;

    /**
     * Registry-backed holder for the corrosion effect, required by
     * {@code MobEffectInstance} and saved/synced by effect id.
     */
    public static Holder<MobEffect> corrosion() {
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(CORROSION);
    }
}
