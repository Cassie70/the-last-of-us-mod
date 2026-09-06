package com.cassie77.the_last_block_of_us.item.micotoxinsac;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public class MycotoxinSacItem extends Item implements ProjectileItem {
    public static float POWER = 1.0F;

    public MycotoxinSacItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            Projectile.spawnProjectileFromRotation(MycotoxinSacEntity::new, serverLevel, itemStack, user, 0.0F, POWER, 1.0F);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        user.getCooldowns().addCooldown(itemStack, 20);
        itemStack.consume(1, user);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        return new MycotoxinSacEntity(level, pos.x(), pos.y(), pos.z(), stack);
    }
}
