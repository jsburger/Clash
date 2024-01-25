package com.jsburg.clash.particle;

import com.jsburg.clash.util.ScreenShaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScreenShakerParticle extends NoRenderParticle {
    private final double shakeIntensity;
    private final double shakeTime;
    private final double falloffDistance;

    protected ScreenShakerParticle(ClientLevel world, double x, double y, double z, double shakeIntensity, double shakeTime, double fallOffDistance) {
        super(world, x, y, z);
        this.shakeIntensity = shakeIntensity;
        this.shakeTime = shakeTime;
        this.falloffDistance = fallOffDistance;
    }


    @Override
    public void tick() {
        this.remove();

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        Vec3 diff = player.position().subtract(this.xo, this.yo, this.zo);
        double intensityScale = Math.pow(1 - Mth.clamp(diff.length()/falloffDistance, 0, 1), 2);
        if (intensityScale > 0)
            ScreenShaker.setScreenShake((int) shakeTime, shakeIntensity * intensityScale);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory(SpriteSet sprite){}

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double shakeIntensity, double shakeTime, double fallOffDistance) {
            return new ScreenShakerParticle(worldIn, x, y, z, shakeIntensity, shakeTime, fallOffDistance);
        }
    }

}
