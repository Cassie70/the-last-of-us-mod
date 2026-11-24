package com.cassie77.the_last_block_of_us.item.molotov;

import com.cassie77.the_last_block_of_us.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MolotovItem extends Item {
    public static float POWER = 1.0F;

    public MolotovItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.THROW_MOLOTOV, SoundSource.NEUTRAL, 0.5F, 1.0F);
        if (!level.isClientSide) {
            MolotovEntity projectile = new MolotovEntity(level, user, itemStack);
            projectile.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, POWER, 1.0F);
            level.addFreshEntity(projectile);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        user.getCooldowns().addCooldown(this, 20);
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        return InteractionResultHolder.success(itemStack);
    }
}
