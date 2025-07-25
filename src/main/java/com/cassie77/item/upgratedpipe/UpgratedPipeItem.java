package com.cassie77.item.upgratedpipe;

import com.cassie77.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class UpgratedPipeItem extends Item {
    public UpgratedPipeItem(Settings settings) {
        super(settings);

    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient && attacker instanceof PlayerEntity player) {
            if(stack.getDamage() >= stack.getMaxDamage()-1) {
                stack.damage(1, player);

                if (!player.getAbilities().creativeMode) {
                    ItemStack reward = new ItemStack(ModItems.PIPE);
                    if (!player.getInventory().insertStack(reward)) {
                        player.dropItem(reward, false);
                    }
                }
                return;
            }
        }
        super.postHit(stack, target, attacker);
    }

}
