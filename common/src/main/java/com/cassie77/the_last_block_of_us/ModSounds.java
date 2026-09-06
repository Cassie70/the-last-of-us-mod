package com.cassie77.the_last_block_of_us;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    private ModSounds() {
    }

    public static final SoundEvent THROW_MOLOTOV = registerSound("throw_molotov");
    public static final SoundEvent EXPLODE_MOLOTOV = registerSound("explode_molotov");

    public static final SoundEvent CLICKER_ANGRY = registerSound("clicker_angry");
    public static final SoundEvent CLICKER_AWARE = registerSound("clicker_aware");
    public static final SoundEvent CLICKER_AMBIENT = registerSound("clicker_ambient");
    public static final SoundEvent CLICKER_AMBIENT_2 = registerSound("clicker_ambient_2");
    public static final SoundEvent CLICKER_ALERT = registerSound("clicker_alert");

    public static final SoundEvent BLOATER_ANGRY = registerSound("bloater_angry");
    public static final SoundEvent BLOATER_AWARE = registerSound("bloater_aware");
    public static final SoundEvent BLOATER_AMBIENT = registerSound("bloater_ambient");
    public static final SoundEvent BLOATER_AMBIENT_2 = registerSound("bloater_ambient_2");
    public static final SoundEvent BLOATER_AMBIENT_3 = registerSound("bloater_ambient_3");
    public static final SoundEvent BLOATER_ALERT = registerSound("bloater_alert");

    private static SoundEvent registerSound(String id) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, id);
        Constants.LOG.info("Registering sound: {}", location);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, location, SoundEvent.createVariableRangeEvent(location));
    }

    public static void initialize() {
        Constants.LOG.info("Registering {} Sounds", Constants.MOD_ID);
    }
}
