package com.cassie77;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static com.cassie77.TheLastOfUsMod.LOGGER;
import static com.cassie77.TheLastOfUsMod.MOD_ID;

public class CustomSounds {
    private CustomSounds() {
        // private empty constructor to avoid accidental instantiation
    }

    // ITEM_METAL_WHISTLE is the name of the custom sound event
    // and is called in the mod to use the custom sound
    public static final SoundEvent THROW_MOLOTOV = registerSound("throw_molotov");
    public static final SoundEvent EXPLODE_MOLOTOV = registerSound("explode_molotov");


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
