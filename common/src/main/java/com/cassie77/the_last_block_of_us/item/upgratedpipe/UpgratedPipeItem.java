package com.cassie77.the_last_block_of_us.item.upgratedpipe;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class UpgratedPipeItem extends SwordItem {
    public UpgratedPipeItem(Properties properties) {
        // 1.20.1 has no Item.Properties.sword(...); the sword behaviour comes from SwordItem itself.
        super(Tiers.IRON, 27, -3.5F, properties);
    }
}
