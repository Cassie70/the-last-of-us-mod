package com.cassie77.the_last_block_of_us.entity.clicker;

import com.cassie77.the_last_block_of_us.ModSounds;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.Arrays;

public enum ClickerAngriness {

    CALM(0, ModSounds.CLICKER_AMBIENT, SoundEvents.WARDEN_LISTENING),
    AGITATED(60, ModSounds.CLICKER_AMBIENT, SoundEvents.WARDEN_LISTENING_ANGRY),
    ANGRY(120, ModSounds.CLICKER_AMBIENT, SoundEvents.WARDEN_LISTENING_ANGRY);

    private static final ClickerAngriness[] VALUES = Util.make(values(), (values) -> Arrays.sort(values, (a, b) -> Integer.compare(b.threshold, a.threshold)));
    private final int threshold;
    private final SoundEvent sound;
    private final SoundEvent listeningSound;

    ClickerAngriness(final int threshold, final SoundEvent sound, final SoundEvent listeningSound) {
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

    public SoundEvent getListeningSound() {
        return this.listeningSound;
    }

    public static ClickerAngriness getForAnger(int anger) {
        for (ClickerAngriness angriness : VALUES) {
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
