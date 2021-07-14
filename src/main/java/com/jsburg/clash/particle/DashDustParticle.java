package com.jsburg.clash.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static java.lang.Math.PI;

public class DashDustParticle extends SpriteTexturedParticle {

    private final int spinDir;
    private int growTime;

    protected DashDustParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite sprite) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.selectSpriteRandomly(sprite);
        this.setMotion(motionX, motionY, motionZ);
        this.particleAngle = (float) (this.rand.nextFloat() * 2 * PI);
        this.prevParticleAngle = particleAngle;
        this.spinDir = (int) ((this.rand.nextInt(1) - .5) * 2);
        this.maxAge = 10 + this.rand.nextInt(4);
        this.growTime = this.rand.nextInt(2) + 1;
        this.particleScale = 0.4f + this.rand.nextFloat() * 0.3f;
    }

    private void setMotion(double X, double Y, double Z) {
        this.motionX = X;
        this.motionY = Y;
        this.motionZ = Z;
    }

    private void scaleMotion(double n) {
        setMotion(this.motionX * n, this.motionY * n, this.motionZ * n);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        if (growTime <= 0) {
            particleScale *= 0.8f;
        }
        else {
            growTime -= 1;
            particleScale *= 1.1;
        }
        prevParticleAngle = particleAngle;
        particleAngle += .1f * spinDir * (1/particleScale);
        this.scaleMotion(.9 - particleScale/10);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DashDustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
        }
    }

}
