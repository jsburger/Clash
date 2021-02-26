package com.jsburg.clash.particle;

import com.jsburg.clash.Clash;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static java.lang.Math.PI;

public class SpearCritParticle extends SpriteTexturedParticle {

    private final int spinDir;

    protected SpearCritParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite sprite) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.selectSpriteWithAge(sprite);
        this.posX += this.motionX * 8;
        this.posY += this.motionY * 8;
        this.posZ += this.motionZ * 8;
        this.motionX *= 0;
        this.motionY *= 0;
        this.motionZ *= 0;
        this.particleAngle = (float) (this.rand.nextFloat() * 2 * PI);
        this.prevParticleAngle = particleAngle;
        this.spinDir = this.rand.nextInt(2) * -2 + 1;
        this.maxAge = 8;
        this.particleScale = 0.3f + this.rand.nextFloat() * 0.2f;
        float f = this.rand.nextFloat() * 0.1f + 0.85f;
        this.setColor(1, f, f);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        this.particleScale *= 0.8f;
        this.prevParticleAngle = particleAngle;
        this.particleAngle += .1f * spinDir * (1/particleScale);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SpearCritParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
        }
    }

}
