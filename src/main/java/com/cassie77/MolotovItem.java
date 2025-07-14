package com.cassie77;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class MolotovItem extends Item implements ProjectileItem {
    public static float POWER = 1.0F;

    public MolotovItem(Item.Settings settings) {
        super(settings);
    }

    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound((Entity)null, user.getX(), user.getY(), user.getZ(), CustomSounds.THROW_MOLOTOV, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        if (world instanceof ServerWorld serverWorld) {
            ProjectileEntity.spawnWithVelocity(MolotovEntity::new, serverWorld, itemStack, user, 0.0F, POWER, 1.0F);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        user.getItemCooldownManager().set(itemStack, 20);
        itemStack.decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }

    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        return new MolotovEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
}
