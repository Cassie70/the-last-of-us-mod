package com.cassie77.the_last_block_of_us.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

/**
 * Dense, slightly yellow mycotoxin smoke used by the corrosion cloud and the
 * shambler explosion.
 *
 * <p>It uses a single sprite at a constant size, is visible from the very first
 * tick and hangs in the air for about 5 seconds like heavy gas: it stays dense
 * for most of its life and then fades out slowly instead of popping away.
 * The tint is {@code 0xEFE6A8}.
 */
public class CorrosionSmokeParticle extends TextureSheetParticle {

    private static final int LIFETIME = 100;
    private static final float SIZE = 0.6F;
    private static final float BASE_ALPHA = 1.0F;
    /** Fraction of the lifetime before the fade-out starts (the last 45%). */
    private static final float FADE_START = 0.55F;
    private static final float RISE_SPEED = 0.02F;
    private static final float TINT_RED = 0xEF / 255.0F;
    private static final float TINT_GREEN = 0xE6 / 255.0F;
    private static final float TINT_BLUE = 0xA8 / 255.0F;

    public CorrosionSmokeParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            SpriteSet sprites
    ) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.lifetime = LIFETIME;
        this.friction = 0.96F;
        // Negative gravity: the puff keeps drifting upwards for its whole life.
        this.gravity = -0.0006F;
        this.hasPhysics = false;
        this.quadSize = SIZE * (0.85F + this.random.nextFloat() * 0.3F);
        this.roll = (this.random.nextFloat() - 0.5F) * 0.4F;
        this.oRoll = this.roll;
        this.setColor(TINT_RED, TINT_GREEN, TINT_BLUE);
        this.setAlpha(BASE_ALPHA);

        this.xd = xSpeed * 0.35 + (this.random.nextDouble() - 0.5) * 0.03;
        this.yd = RISE_SPEED + this.random.nextDouble() * 0.015;
        this.zd = zSpeed * 0.35 + (this.random.nextDouble() - 0.5) * 0.03;

        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.removed) {
            return;
        }

        float progress = this.age / (float) this.lifetime;
        if (progress > FADE_START) {
            float fade = (progress - FADE_START) / (1.0F - FADE_START);
            // Smoothstep: holds nearly full opacity at first and releases it gently.
            float eased = fade * fade * (3.0F - 2.0F * fade);
            this.setAlpha(BASE_ALPHA * (1.0F - eased));
        }

        this.oRoll = this.roll;
        this.roll += (this.random.nextFloat() - 0.5F) * 0.012F;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                @NotNull SimpleParticleType type,
                @NotNull ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            return new CorrosionSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
