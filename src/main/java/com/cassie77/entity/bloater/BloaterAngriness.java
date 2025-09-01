package com.cassie77.entity.bloater;

import com.cassie77.ModSounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

import java.util.Arrays;

public enum BloaterAngriness {

    CALM(0, ModSounds.BLOATER_AMBIENT, SoundEvents.ENTITY_WARDEN_LISTENING),
    AGITATED(60, ModSounds.BLOATER_AMBIENT, SoundEvents.ENTITY_WARDEN_LISTENING_ANGRY),
    ANGRY(120, ModSounds.BLOATER_AMBIENT, SoundEvents.ENTITY_WARDEN_LISTENING_ANGRY);

    private static final BloaterAngriness[] VALUES = Util.make(values(), (values) -> Arrays.sort(values, (a, b) -> Integer.compare(b.threshold, a.threshold)));
    private final int threshold;
    private final SoundEvent sound;
    private final SoundEvent listeningSound;

    BloaterAngriness(final int threshold, final SoundEvent sound, final SoundEvent listeningSound) {
        this.threshold = threshold;
        this.sound = sound;
        this.listeningSound = listeningSound;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public SoundEvent e() {
        return this.listeningSound;
    }

    public static BloaterAngriness getForAnger(int anger) {
        for(BloaterAngriness angriness : VALUES) {
            if (anger >= angriness.threshold) {
                return angriness;
            }
        }

        return CALM;
    }

    public boolean isAngry() {
        return this == ANGRY;
    }
}
