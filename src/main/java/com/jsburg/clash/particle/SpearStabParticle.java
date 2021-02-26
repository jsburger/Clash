package com.jsburg.clash.particle;

import com.jsburg.clash.Clash;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static java.lang.Math.PI;
import static java.lang.Math.signum;

@OnlyIn(Dist.CLIENT)
public class SpearStabParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite spriteWithAge;
    private static final Vector3d UP = new Vector3d(0, 1, 0);

    protected SpearStabParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.motionX = xSpeed;
        this.motionY = ySpeed;
        this.motionZ = zSpeed;
        this.spriteWithAge = spriteWithAge;
        this.maxAge = 5;
        float f = this.rand.nextFloat() * 0.2F + 0.8F;
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;
        this.particleScale = 0.8f;
        this.selectSpriteWithAge(spriteWithAge);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 15728880;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.selectSpriteWithAge(this.spriteWithAge);
            this.move(this.motionX, this.motionY, this.motionZ);

            this.motionX *= 0.8f;
            this.motionY *= 0.8f;
            this.motionZ *= 0.8f;
            this.particleScale *= 0.9f;

            if (this.onGround) {
                this.motionX *= 0.7F;
                this.motionZ *= 0.7F;
            }
        }
    }


    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        Vector3d viewAngle = new Vector3d(renderInfo.getViewVector());
        if (viewAngle.getY() != 1) {
            Vector3d motion = new Vector3d(this.motionX, this.motionY, this.motionZ).normalize();
            Vector3d particlePos = new Vector3d(this.posX, this.posY, this.posZ);
            Vector3d viewPos = renderInfo.getProjectedView();
            Vector3d relativePos = particlePos.subtract(viewPos).normalize();

            Vector3d particleInView = relativePos.crossProduct(motion).crossProduct(viewAngle);
            Vector3d xPlane = viewAngle.crossProduct(UP);
            Vector3d yPlane = viewAngle.crossProduct(xPlane);

            double xValue = particleInView.dotProduct(xPlane);
            double yValue = particleInView.dotProduct(yPlane);

            this.particleAngle = (float) (Math.atan2(yValue, xValue) + PI/2);
            this.prevParticleAngle = this.particleAngle;
        }
        super.renderParticle(buffer, renderInfo, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SpearStabParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

}
