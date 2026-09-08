package com.cassie77.the_last_block_of_us.entity.infected;

import net.minecraft.Util;

import java.util.Arrays;

public enum InfectedAngriness {

    CALM(0),
    AGITATED(60),
    ANGRY(120);

    private static final InfectedAngriness[] VALUES = Util.make(
            values(),
            values -> Arrays.sort(
                    values,
                    (a, b) -> Integer.compare(b.threshold, a.threshold)
            )
    );

    private final int threshold;

    InfectedAngriness(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }

    public static InfectedAngriness getForAnger(int anger) {
        for (InfectedAngriness angriness : VALUES) {
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
