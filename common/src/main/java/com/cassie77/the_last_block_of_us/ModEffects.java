package com.cassie77.the_last_block_of_us;

import net.minecraft.world.effect.MobEffect;

public class ModEffects {

    /**
     * Assigned by the loader-specific registration class
     * ({@code FabricModEffects}, {@code ForgeModEffects}).
     */
    public static MobEffect CORROSION;

    /**
     * 1.20.1 has no registry-backed {@code Holder<MobEffect>}: {@code MobEffectInstance}
     * takes the effect directly, so this simply returns the registered instance.
     */
    public static MobEffect corrosion() {
        return CORROSION;
    }
}
