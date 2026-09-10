package com.cassie77.the_last_block_of_us;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public final class NeoForgeModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT,
            Constants.MOD_ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> THROW_MOLOTOV = register("throw_molotov");
    public static final DeferredHolder<SoundEvent, SoundEvent> EXPLODE_MOLOTOV = register("explode_molotov");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLICKER_ANGRY = register("clicker_angry");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLICKER_AWARE = register("clicker_aware");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLICKER_AMBIENT = register("clicker_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLICKER_AMBIENT_2 = register("clicker_ambient_2");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLICKER_ALERT = register("clicker_alert");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLICKER_ALERT_ANGRY = register("clicker_alert_angry");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLICKER_ALERT_ANGRY_2 = register(
            "clicker_alert_angry_2");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOATER_ANGRY = register("bloater_angry");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOATER_AWARE = register("bloater_aware");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOATER_AMBIENT = register("bloater_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOATER_AMBIENT_2 = register("bloater_ambient_2");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOATER_AMBIENT_3 = register("bloater_ambient_3");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOATER_ALERT = register("bloater_alert");

    private NeoForgeModSounds() {
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> {
            SoundEvent sound = SoundEvent
                    .createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
            switch (name) {
                case "throw_molotov" -> ModSounds.THROW_MOLOTOV = sound;
                case "explode_molotov" -> ModSounds.EXPLODE_MOLOTOV = sound;
                case "clicker_angry" -> ModSounds.CLICKER_ANGRY = sound;
                case "clicker_aware" -> ModSounds.CLICKER_AWARE = sound;
                case "clicker_ambient" -> ModSounds.CLICKER_AMBIENT = sound;
                case "clicker_ambient_2" -> ModSounds.CLICKER_AMBIENT_2 = sound;
                case "clicker_alert" -> ModSounds.CLICKER_ALERT = sound;
                case "clicker_alert_angry" -> ModSounds.CLICKER_ALERT_ANGRY = sound;
                case "clicker_alert_angry_2" -> ModSounds.CLICKER_ALERT_ANGRY_2 = sound;
                case "bloater_angry" -> ModSounds.BLOATER_ANGRY = sound;
                case "bloater_aware" -> ModSounds.BLOATER_AWARE = sound;
                case "bloater_ambient" -> ModSounds.BLOATER_AMBIENT = sound;
                case "bloater_ambient_2" -> ModSounds.BLOATER_AMBIENT_2 = sound;
                case "bloater_ambient_3" -> ModSounds.BLOATER_AMBIENT_3 = sound;
                case "bloater_alert" -> ModSounds.BLOATER_ALERT = sound;
            }
            return sound;
        });
    }
}