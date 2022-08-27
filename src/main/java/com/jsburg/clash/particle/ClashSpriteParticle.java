package com.jsburg.clash.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
//Multipurpose class that does basic effects.
public class ClashSpriteParticle extends SpriteTexturedParticle {

    public static Factory BonusDrop(IAnimatedSprite sprite) {
        return new Factory(sprite, (particle) -> {});
    }
    public static Factory ButcherSpark(IAnimatedSprite sprite) {
        return new Factory(sprite, (p) -> {
            p.maxAge = 8;
            p.particleScale = 0.3f + p.rand.nextFloat() * 0.1f;
        });
    }


    private final IAnimatedSprite spriteWithAge;

    protected ClashSpriteParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite sprite) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.selectSpriteWithAge(sprite);
        this.maxAge = 20;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.spriteWithAge = sprite;
        this.particleScale = 0.3f;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.selectSpriteWithAge(this.spriteWithAge);
        }

    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;
        private final Consumer<ClashSpriteParticle> onParticleCreate;

        public Factory(IAnimatedSprite sprite, Consumer<ClashSpriteParticle> onParticleInit) {
            this.sprite = sprite;
            this.onParticleCreate = onParticleInit;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ClashSpriteParticle p = new ClashSpriteParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
            onParticleCreate.accept(p);
            return p;
        }
    }


}
