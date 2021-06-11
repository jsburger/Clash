package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.particle.AxeSweepParticle;
import com.jsburg.clash.particle.SpearCritParticle;
import com.jsburg.clash.particle.SpearStabParticle;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.weapons.SpearItem;
import com.jsburg.clash.weapons.SweptAxeItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static net.minecraft.util.math.MathHelper.sqrt;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    private static final Random ScreenShakeRandom = new Random();
    private static double ScreenShakeX = 0;
    private static double ScreenShakeY = 0;
    private static double ScreenShakeAmount = 0;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        ParticleManager manager = Minecraft.getInstance().particles;
        manager.registerFactory(AllParticles.SPEAR_STAB.get(), SpearStabParticle.Factory::new);
        manager.registerFactory(AllParticles.SPEAR_CRIT.get(), SpearCritParticle.Factory::new);
        manager.registerFactory(AllParticles.AXE_SWEEP.get(), AxeSweepParticle.Factory::new);
    }

    public static void doClientTick(TickEvent.ClientTickEvent event) {
        if (ScreenShakeAmount-- > 1) {
            ScreenShakeX = ((ScreenShakeRandom.nextDouble() * 2) - 1)/3;
            ScreenShakeY = ((ScreenShakeRandom.nextDouble() * 2) - 1)/6;
        }
    }

    public static void setScreenShake(double intensity) {
        ScreenShakeAmount = intensity;
    }

    public static void doCameraStuff(EntityViewRenderEvent event) {
        if (ScreenShakeAmount > 0) {
            ActiveRenderInfo renderInfo = event.getInfo();
            double ticks = (event.getRenderPartialTicks() - .5) * 2;
            renderInfo.movePosition(0, ScreenShakeY * ticks, ScreenShakeX * ticks);
        }
    }

    public static void fiddleWithHands(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;
        boolean leftHanded = player.getPrimaryHand() == HandSide.LEFT ^ event.getHand() == Hand.OFF_HAND;
        if (event.getItemStack().getItem() instanceof SpearItem) {
            if (event.getHand() == player.getActiveHand() && player.isHandActive()) {
                SpearItem spear = (SpearItem) event.getItemStack().getItem();
                int useCount = player.getItemInUseCount();
                int useTime = spear.getUseDuration(event.getItemStack()) - useCount;
                float chargePercent = (float) Math.pow(Math.min((useTime + event.getPartialTicks()) / spear.getMaxCharge(event.getItemStack()), 1), 1);
                int sideFlip = leftHanded ? -1 : 1;

                float xAngle = -6 * chargePercent;
                float yAngle = 0 * chargePercent;
                float zAngle = 20 * chargePercent * sideFlip;
//                if (useTime > 20) {
//                    zAngle += 10;
//                    yAngle += 3;
//                    xAngle -= 10;
//                    event.getMatrixStack().translate(.1, 0, 0);
//                }
                event.getMatrixStack().rotate(new Quaternion(xAngle, yAngle, zAngle, true));
            }
        }
        if (event.getHand() == Hand.MAIN_HAND && event.getItemStack().getItem() instanceof SweptAxeItem && event.getEquipProgress() > .1) {
            float swingProgress = event.getSwingProgress();
            HandSide side = player.getPrimaryHand();
            MatrixStack stack = event.getMatrixStack();
            int sideFlip = leftHanded ? -1 : 1;

            FirstPersonRenderer renderer = Minecraft.getInstance().getFirstPersonRenderer();
            ItemCameraTransforms.TransformType transform = leftHanded ? ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;

            float pi = (float)Math.PI;

            stack.push();

            float xOffset =-1.4F * MathHelper.sin((swingProgress) * pi);
            float yOffset = 0.2F * MathHelper.sin(sqrt(swingProgress) * pi * 2F);
            float zOffset =-0.6F * MathHelper.sin((swingProgress) * pi * 2);
            stack.translate(sideFlip * xOffset, yOffset, zOffset);

            transformSideFirstPerson(stack, side, (swingProgress == 0 || swingProgress > .7 || event.getEquipProgress() == 0) ? event.getEquipProgress() : 0);
            stack.rotate(Vector3f.ZP.rotationDegrees(sideFlip * -80 * MathHelper.sin(sqrt(swingProgress) * pi)));
            stack.rotate(Vector3f.XP.rotationDegrees(-180 * MathHelper.sin(swingProgress * pi)));

            renderer.renderItemSide(player, event.getItemStack(), transform, leftHanded, stack, event.getBuffers(), event.getLight());
            stack.pop();

            event.setCanceled(true);
        }

    }

    private static void transformFirstPerson(MatrixStack matrixStackIn, HandSide handIn, float swingProgress) {
        int i = handIn == HandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)i * (45.0F + f * -20.0F)));
        float f1 = MathHelper.sin(sqrt(swingProgress) * (float)Math.PI);
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i * f1 * -20.0F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f1 * -80.0F));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)i * -45.0F));
    }

    private static void transformSideFirstPerson(MatrixStack matrixStackIn, HandSide handIn, float equippedProg) {
        int i = handIn == HandSide.RIGHT ? 1 : -1;
        matrixStackIn.translate(((float)i * 0.56F), (-0.52F + equippedProg * -0.6F), -0.72F);
    }

}

