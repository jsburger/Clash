package com.jsburg.clash.particle;

import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ButcherSparkEmitter extends MetaParticle {

    private int timeSinceStart;
    private final int maximumTime = 3;

    protected ButcherSparkEmitter(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z, 0, 0, 0);
    }

    @Override
    public void tick() {
        for(int i = 0; i < 1+this.rand.nextInt(1); ++i) {
            double n = 1.0D;
            double d0 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * n;
            double d1 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * n;
            double d2 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * n;
            Vector3d pos = new Vector3d(d0, d1, d2);
            AttackHelper.makeParticle(this.world, AllParticles.BUTCHER_SPARK.get(), pos);
        }

        ++this.timeSinceStart;
        if (this.timeSinceStart == this.maximumTime) {
            this.setExpired();
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        public Factory(IAnimatedSprite sprite){}

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ButcherSparkEmitter(worldIn, x, y, z);
        }
    }


}
