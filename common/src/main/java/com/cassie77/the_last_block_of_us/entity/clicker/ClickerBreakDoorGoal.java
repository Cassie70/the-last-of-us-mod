package com.cassie77.the_last_block_of_us.entity.clicker;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;

import java.util.function.Predicate;

public class ClickerBreakDoorGoal extends BreakDoorGoal {

    private final ClickerEntity clicker;

    public ClickerBreakDoorGoal(ClickerEntity mob, Predicate<Difficulty> difficultySufficientPredicate) {
        super(mob, difficultySufficientPredicate);
        this.clicker = mob;
    }

    @Override
    public boolean canUse() {
        if (!clicker.getAngriness().isAngry()) {
            return false;
        }
        return super.canUse();
    }

    @Override
    public void start() {
        super.start();
        clicker.level().broadcastEntityEvent(clicker, (byte) 5);
    }
}
