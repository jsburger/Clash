package com.jsburg.clash.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AxeSweepParticle extends TextureSheetParticle {

    private final SpriteSet spriteWithAge;
    private final boolean flip;

    private AxeSweepParticle(ClientLevel world, double x, double y, double z, double scale, boolean isRed, boolean flipped, SpriteSet spriteWithAge) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.spriteWithAge = spriteWithAge;
        this.lifetime = 5;
        float f = this.random.nextFloat() * 0.2F + 0.8F;
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        if (isRed) {
            this.rCol = .7f;
            this.gCol *= .15;
            this.bCol *= .15;
        }

        this.quadSize = 1.0F - (float)scale * 0.2F;
        this.flip = flipped;
        this.setSpriteFromAge(spriteWithAge);
    }
    private AxeSweepParticle(ClientLevel world, double x, double y, double z, boolean isBlue, boolean flipped, float rotation, SpriteSet spriteWithAge){
        this(world, x, y, z, 1, false, flipped, spriteWithAge);
        if (isBlue) {
            this.bCol = 1f;
            this.gCol *= .95;
            this.rCol *= .75;
        }
        if (flipped) {
            rotation = -rotation;
        }
        this.roll = rotation;
        this.oRoll = rotation;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.spriteWithAge);
        }
    }


    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double scale, double isRed, double isFlipped) {
            return new AxeSweepParticle(worldIn, x, y, z, scale, isRed > 0, isFlipped > 0, this.spriteSet);
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static class BladeFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public BladeFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double isExecutioner, double isBlue, double isFlipped) {
            return new AxeSweepParticle(worldIn, x, y, z, isBlue > 0, isFlipped > 0, isExecutioner > 0 ? -45 * (0.0174533f) : 0, this.spriteSet);
        }
    }

    protected float getU0() {
        return this.flip ? super.getU1() : super.getU0();
    }

    protected float getU1() {
        return this.flip ? super.getU0() : super.getU1();
    }

}
