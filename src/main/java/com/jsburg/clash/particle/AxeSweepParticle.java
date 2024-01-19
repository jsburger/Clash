package com.jsburg.clash.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AxeSweepParticle extends SpriteTexturedParticle {

    private final IAnimatedSprite spriteWithAge;
    private final boolean flip;

    private AxeSweepParticle(ClientWorld world, double x, double y, double z, double scale, boolean isRed, boolean flipped, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.spriteWithAge = spriteWithAge;
        this.maxAge = 5;
        float f = this.rand.nextFloat() * 0.2F + 0.8F;
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;
        if (isRed) {
            this.particleRed = .7f;
            this.particleGreen *= .15;
            this.particleBlue *= .15;
        }
        this.particleScale = 1.0F - (float)scale * 0.2F;
        this.flip = flipped;
        this.selectSpriteWithAge(spriteWithAge);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 15728880;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.selectSpriteWithAge(this.spriteWithAge);
        }
    }


    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double scale, double isRed, double isFlipped) {
            return new AxeSweepParticle(worldIn, x, y, z, scale, isRed > 0, isFlipped > 0, this.spriteSet);
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static class BladeFactory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public BladeFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double scale, double isRed, double isFlipped) {
            return new AxeSweepParticle(worldIn, x, y, z, scale, isRed > 0, isFlipped > 0, this.spriteSet);
        }
    }

    protected float getMinU() {
        return this.flip ? super.getMaxU() : super.getMinU();
    }

    protected float getMaxU() {
        return this.flip ? super.getMinU() : super.getMaxU();
    }

}
