package com.cassie77;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static com.cassie77.TheLastOfUsMod.LOGGER;
import static com.cassie77.TheLastOfUsMod.MOD_ID;

public class ModSounds {
    private ModSounds() {
        // private empty constructor to avoid accidental instantiation
    }

    // ITEM_METAL_WHISTLE is the name of the custom sound event
    // and is called in the mod to use the custom sound
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

    // actual registration of all the custom SoundEvents
    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(MOD_ID, id);
        LOGGER.info("Registering sound: " + identifier);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    // This static method starts class initialization, which then initializes
    // the static class variables (e.g. ITEM_METAL_WHISTLE).
    public static void initialize() {
        TheLastOfUsMod.LOGGER.info("Registering " + TheLastOfUsMod.MOD_ID + " Sounds");
    }
}
