package com.jsburg.clash.particle;

import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ButcherSparkEmitter extends NoRenderParticle {

    private int timeSinceStart;
    private final int maximumTime = 3;

    protected ButcherSparkEmitter(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z, 0, 0, 0);
    }

    @Override
    public void tick() {
        for(int i = 0; i < 1+this.random.nextInt(1); ++i) {
            double n = 1.0D;
            double d0 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * n;
            double d1 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * n;
            double d2 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * n;
            Vec3 pos = new Vec3(d0, d1, d2);
            AttackHelper.makeParticle(this.level, AllParticles.BUTCHER_SPARK.get(), pos);
        }

        ++this.timeSinceStart;
        if (this.timeSinceStart == this.maximumTime) {
            this.remove();
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory(SpriteSet sprite){}

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ButcherSparkEmitter(worldIn, x, y, z);
        }
    }


}
