package com.jsburg.clash.particle;

import com.jsburg.clash.util.ScreenShaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.MetaParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScreenShakerParticle extends MetaParticle {
    private final double shakeIntensity;
    private final double shakeTime;
    private final double falloffDistance;

    protected ScreenShakerParticle(ClientWorld world, double x, double y, double z, double shakeIntensity, double shakeTime, double fallOffDistance) {
        super(world, x, y, z);
        this.shakeIntensity = shakeIntensity;
        this.shakeTime = shakeTime;
        this.falloffDistance = fallOffDistance;
    }


    @Override
    public void tick() {
        this.setExpired();

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) return;
        Vector3d diff = player.getPositionVec().subtract(this.prevPosX, this.prevPosY, this.prevPosZ);
        double intensityScale = Math.pow(1 - MathHelper.clamp(diff.length()/falloffDistance, 0, 1), 2);
        if (intensityScale > 0)
            ScreenShaker.setScreenShake((int) shakeTime, shakeIntensity * intensityScale);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        public Factory(IAnimatedSprite sprite){}

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double shakeIntensity, double shakeTime, double fallOffDistance) {
            return new ScreenShakerParticle(worldIn, x, y, z, shakeIntensity, shakeTime, fallOffDistance);
        }
    }

}
