package com.jsburg.clash.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
//Multipurpose class that does basic effects.
public class ClashSpriteParticle extends TextureSheetParticle {

    public static Factory BonusDrop(SpriteSet sprite) {
        return new Factory(sprite, (particle) -> {});
    }
    public static Factory ButcherSpark(SpriteSet sprite) {
        return new Factory(sprite, (p) -> {
            p.lifetime = 6;
            p.quadSize = 0.3f + p.random.nextFloat() * 0.1f;
        });
    }
    public static Factory SailingTrail(SpriteSet sprite) {
        return new Factory(sprite, (p) -> {
            p.lifetime = 14;
            p.quadSize = 1f;
        });
    }


    private final SpriteSet spriteWithAge;

    protected ClashSpriteParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprite) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.setSpriteFromAge(sprite);
        this.lifetime = 20;
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.spriteWithAge = sprite;
        this.quadSize = 0.3f;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.spriteWithAge);
        }

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        private final Consumer<ClashSpriteParticle> onParticleCreate;

        public Factory(SpriteSet sprite, Consumer<ClashSpriteParticle> onParticleInit) {
            this.sprite = sprite;
            this.onParticleCreate = onParticleInit;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ClashSpriteParticle p = new ClashSpriteParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
            onParticleCreate.accept(p);
            return p;
        }
    }


}
