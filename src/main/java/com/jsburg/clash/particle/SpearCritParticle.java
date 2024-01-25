package com.jsburg.clash.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static java.lang.Math.PI;

public class SpearCritParticle extends TextureSheetParticle {

    private final int spinDir;

    protected SpearCritParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprite) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.setSpriteFromAge(sprite);
        this.x += this.xd * 8;
        this.y += this.yd * 8;
        this.z += this.zd * 8;
        this.xd *= 0;
        this.yd *= 0;
        this.zd *= 0;
        this.roll = (float) (this.random.nextFloat() * 2 * PI);
        this.oRoll = roll;
        this.spinDir = this.random.nextInt(2) * -2 + 1;
        this.lifetime = 8;
        this.quadSize = 0.3f + this.random.nextFloat() * 0.2f;
        float f = this.random.nextFloat() * 0.1f + 0.85f;
        this.setColor(1, f, f);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        this.quadSize *= 0.8f;
        this.oRoll = roll;
        this.roll += .1f * spinDir * (1/quadSize);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SpearCritParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
        }
    }

}
