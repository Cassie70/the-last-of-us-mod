package com.cassie77.the_last_block_of_us;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class FabricModSounds {

    private FabricModSounds() {
    }

    public static void initialize() {
        ModSounds.THROW_MOLOTOV = register("throw_molotov");
        ModSounds.EXPLODE_MOLOTOV = register("explode_molotov");
        ModSounds.CLICKER_ANGRY = register("clicker_angry");
        ModSounds.CLICKER_AWARE = register("clicker_aware");
        ModSounds.CLICKER_AMBIENT = register("clicker_ambient");
        ModSounds.CLICKER_AMBIENT_2 = register("clicker_ambient_2");
        ModSounds.CLICKER_ALERT = register("clicker_alert");
        ModSounds.CLICKER_ALERT_ANGRY = register("clicker_alert_angry");
        ModSounds.CLICKER_ALERT_ANGRY_2 = register("clicker_alert_angry_2");
        ModSounds.BLOATER_ANGRY = register("bloater_angry");
        ModSounds.BLOATER_AWARE = register("bloater_aware");
        ModSounds.BLOATER_AMBIENT = register("bloater_ambient");
        ModSounds.BLOATER_AMBIENT_2 = register("bloater_ambient_2");
        ModSounds.BLOATER_AMBIENT_3 = register("bloater_ambient_3");
        ModSounds.BLOATER_ALERT = register("bloater_alert");
    }

    private static SoundEvent register(String name) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, location,
                SoundEvent.createVariableRangeEvent(location));
    }
}
