package com.cassie77.item.molotov;

import com.cassie77.ModSounds;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
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

public class MolotovItem extends Item implements ProjectileItem {
    public static float POWER = 1.0F;

    public MolotovItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        level.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.THROW_MOLOTOV, SoundSource.NEUTRAL, 0.5F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            Projectile.spawnProjectileFromRotation(MolotovEntity::new, serverLevel, itemStack, user, 0.0F, POWER, 1.0F);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        user.getCooldowns().addCooldown(itemStack, 20);
        itemStack.consume(1, user);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        return new MolotovEntity(level, pos.x(), pos.y(), pos.z(), stack);
    }
}
