package com.cassie77.entity.clicker;

import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.Difficulty;

import java.util.function.Predicate;

public class ClickerBreakDoorGoal extends BreakDoorGoal {

    private final ClickerEntity clicker;

    public ClickerBreakDoorGoal(ClickerEntity mob, Predicate<Difficulty> difficultySufficientPredicate) {
        super(mob, difficultySufficientPredicate);
        this.clicker = mob;
    }

    @Override
    public boolean canStart() {
        if(!clicker.getAngriness().isAngry()){
            return false;
        }
        return super.canStart();
    }

    @Override
    public void start() {
        super.start();
        clicker.getWorld().sendEntityStatus(clicker, (byte)5);
    }

}
