package com.jsburg.clash.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static java.lang.Math.PI;

@OnlyIn(Dist.CLIENT)
public class SpearStabParticle extends TextureSheetParticle {
    private final SpriteSet spriteWithAge;
    private static final Vec3 UP = new Vec3(0, 1, 0);

    protected SpearStabParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteWithAge) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.spriteWithAge = spriteWithAge;
        this.lifetime = 5;
        float f = this.random.nextFloat() * 0.2F + 0.8F;
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        this.quadSize = 0.8f;
        this.setSpriteFromAge(spriteWithAge);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.spriteWithAge);
            this.move(this.xd, this.yd, this.zd);

            this.xd *= 0.8f;
            this.yd *= 0.8f;
            this.zd *= 0.8f;
            this.quadSize *= 0.9f;

            if (this.onGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
    }


    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Vec3 viewAngle = new Vec3(renderInfo.getLookVector());
        if (viewAngle.y() != 1) {
            Vec3 motion = new Vec3(this.xd, this.yd, this.zd).normalize();
            Vec3 particlePos = new Vec3(this.x, this.y, this.z);
            Vec3 viewPos = renderInfo.getPosition();
            Vec3 relativePos = particlePos.subtract(viewPos).normalize();

            Vec3 particleInView = relativePos.cross(motion).cross(viewAngle);
            Vec3 xPlane = viewAngle.cross(UP);
            Vec3 yPlane = viewAngle.cross(xPlane);

            double xValue = particleInView.dot(xPlane);
            double yValue = particleInView.dot(yPlane);

            this.roll = (float) (Math.atan2(yValue, xValue) + PI/2);
            this.oRoll = this.roll;
        }
        super.render(buffer, renderInfo, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SpearStabParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

}
