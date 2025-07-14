package com.cassie77.molotov;

import com.cassie77.CustomSounds;
import com.cassie77.ModEntities;
import com.cassie77.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MolotovEntity extends ThrownItemEntity {
    public MolotovEntity(EntityType<? extends MolotovEntity> entityType, World world) {
        super(entityType, world);
    }

    public MolotovEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.MOLOTOV_ENTITY, owner, world, stack);
    }

    public MolotovEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.MOLOTOV_ENTITY, x, y, z, world, stack);
    }

    protected Item getDefaultItem() {
        return ModItems.MOLOTOV;
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getStack();
        return (ParticleEffect)(itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for(int i = 0; i < 8; ++i) {
                this.getWorld().addParticleClient(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }

    }
    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte)3);

            ((ServerWorld) this.getWorld()).spawnParticles(
                    ParticleTypes.LAVA,
                    this.getX() + 0.5,
                    this.getY() + 0.5,
                    this.getZ() + 0.5,
                    100, // count
                    0, 0.5, 0, // offset X,Y,Z
                    0.0 // speed
            );


            this.getWorld().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    CustomSounds.EXPLODE_MOLOTOV,
                    SoundCategory.NEUTRAL,
                    2.0F, 1.0F
            );

            spawnMolotovFire();
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.getWorld().isClient) {
            Entity entity = entityHitResult.getEntity();

            float damageAmount = 15.0F;
            if(this.getWorld() instanceof ServerWorld serverWorld)
                entity.damage(serverWorld,this.getWorld().getDamageSources().generic(), damageAmount);
            entity.setOnFireFor(10);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            // Coordenadas con un poco de aleatoriedad para un efecto más natural
            double offsetX = (this.random.nextDouble() - 0.5) * 0.2;
            double offsetY = (this.random.nextDouble() - 0.5) * 0.2;
            double offsetZ = (this.random.nextDouble() - 0.5) * 0.2;

            this.getWorld().addParticleClient(
                    ParticleTypes.FLAME,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0.0, 0.0, 0.0
            );
        }
    }

    private void spawnMolotovFire() {
        BlockPos center = this.getBlockPos();

        // Alinear al suelo más cercano debajo de la entidad
        while (center.getY() > this.getWorld().getBottomY() &&
                this.getWorld().getBlockState(center).isAir()) {
            center = center.down();
        }
        center = center.up(); // coloca el centro en el aire sobre el bloque sólido

        int radius = 3; // Ajusta el radio deseado

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius) {
                    for (int dy = -1; dy <= 1; dy++) { // Explora Y-1, Y, Y+1
                        BlockPos pos = center.add(dx, dy, dz);
                        if (!this.getWorld().getBlockState(pos.down()).isAir() &&
                                this.getWorld().getBlockState(pos).isAir()) {
                                this.getWorld().setBlockState(pos, Blocks.FIRE.getDefaultState());
                        }
                    }
                }
            }
        }
    }

}
