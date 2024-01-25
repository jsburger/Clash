package com.jsburg.clash.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static java.lang.Math.PI;

public class DashDustParticle extends TextureSheetParticle {

    private final int spinDir;
    private int growTime;

    protected DashDustParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprite) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.pickSprite(sprite);
        this.setMotion(motionX, motionY, motionZ);
        this.roll = (float) (this.random.nextFloat() * 2 * PI);
        this.oRoll = roll;
        this.spinDir = (int) ((this.random.nextInt(1) - .5) * 2);
        this.lifetime = 10 + this.random.nextInt(4);
        this.growTime = this.random.nextInt(2) + 1;
        this.quadSize = 0.4f + this.random.nextFloat() * 0.3f;
    }

    private void setMotion(double X, double Y, double Z) {
        this.xd = X;
        this.yd = Y;
        this.zd = Z;
    }

    private void scaleMotion(double n) {
        setMotion(this.xd * n, this.yd * n, this.zd * n);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        if (growTime <= 0) {
            quadSize *= 0.8f;
        }
        else {
            growTime -= 1;
            quadSize *= 1.1;
        }
        oRoll = roll;
        roll += .1f * spinDir * (1/quadSize);
        this.scaleMotion(.9 - quadSize/10);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DashDustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
        }
    }

}
