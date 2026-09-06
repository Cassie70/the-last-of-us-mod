package com.cassie77.the_last_block_of_us.item.upgratedpipe;

import com.cassie77.the_last_block_of_us.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class UpgratedPipeItem extends Item {

    public UpgratedPipeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide() && attacker instanceof Player player) {
            if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

                if (!player.getAbilities().instabuild) {
                    ItemStack reward = new ItemStack(ModItems.PIPE);
                    if (!player.getInventory().add(reward)) {
                        player.drop(reward, false);
                    }
                }
                return;
            }
        }
        super.postHurtEnemy(stack, target, attacker);
    }
}
