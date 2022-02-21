package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.particle.*;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.util.ScreenShaker;
import com.jsburg.clash.weapons.SpearItem;
import com.jsburg.clash.weapons.SweptAxeItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.util.math.MathHelper.sqrt;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    private static boolean needsPop = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        ParticleManager manager = Minecraft.getInstance().particles;
        manager.registerFactory(AllParticles.SPEAR_STAB.get(), SpearStabParticle.Factory::new);
        manager.registerFactory(AllParticles.SPEAR_CRIT.get(), SpearCritParticle.Factory::new);
        manager.registerFactory(AllParticles.AXE_SWEEP.get(), AxeSweepParticle.Factory::new);
        manager.registerFactory(AllParticles.BUTCHER_SPARK.get(), ButcherSparkParticle.Factory::new);
        manager.registerFactory(AllParticles.BUTCHER_SPARK_EMITTER.get(), ButcherSparkEmitter.Factory::new);
        manager.registerFactory(AllParticles.DASH_DUST.get(), DashDustParticle.Factory::new);
        manager.registerFactory(AllParticles.BONUS_DROP.get(), BonusDropParticle.Factory::new);
        manager.registerFactory(AllParticles.SCREEN_SHAKER.get(), ScreenShakerParticle.Factory::new);
    }

    //All the events below this are set up with listeners in Client setup

    public static void doClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ScreenShaker.tick();
        }
    }

    public static void doCameraStuff(EntityViewRenderEvent.CameraSetup event) {
        ScreenShaker.applyScreenShake(event.getInfo(), event.getRenderPartialTicks());
        //Just resetting this between frames because if execution order *does* get weird I don't want stuff carrying over
        needsPop = false;
    }

    public static void fiddleWithHands(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;
        //Am I relying on execution order staying consistent? Yes.
        if (needsPop) {
            event.getMatrixStack().pop();
            needsPop = false;
        }
        boolean leftHanded = player.getPrimaryHand() == HandSide.LEFT ^ event.getHand() == Hand.OFF_HAND;
        if (event.getItemStack().getItem() instanceof SpearItem) {

            boolean isActive = event.getHand() == player.getActiveHand() && player.isHandActive();
            float cooldown = player.getCooldownTracker().getCooldown(event.getItemStack().getItem(), event.getPartialTicks());
            boolean isCoolingDown = cooldown > 0 && !isActive && !player.isSwingInProgress;
            boolean goingToMove = isActive || isCoolingDown;

            //Main hand always renders first, so it needs to remove its transformations.
            //If the offhand translates stuff it doesn't matter since its rendered last.
            if (goingToMove && event.getHand() == Hand.MAIN_HAND) {
                event.getMatrixStack().push();
                needsPop = true;
            }
            int sideFlip = leftHanded ? -1 : 1;

            if (isActive) {
                SpearItem spear = (SpearItem) event.getItemStack().getItem();
                int useCount = player.getItemInUseCount();
                int useDuration = spear.getUseDuration(event.getItemStack());
                int useTime = useDuration - useCount;
                float chargePercent = (float) Math.pow(Math.min((useTime + event.getPartialTicks()) / spear.getMaxCharge(event.getItemStack()), 1), 1);


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

                double chargeOver = ((useTime + event.getPartialTicks()) - (spear.getMaxCharge(event.getItemStack()) - 4)) / 4;
                chargeOver = Math.pow(Math.max(0, Math.min(1, chargeOver)), 2);
                if (chargeOver > 0) {
                    event.getMatrixStack().translate(0, 0, .1 * chargeOver);
                }
            }

            if (isCoolingDown) {
                MatrixStack stack = event.getMatrixStack();
                float cd = (float) Math.pow(cooldown, 3);

                stack.rotate(new Quaternion(-10 * cd, 4 * cd, 0, true));
                stack.translate(0, -.1 * cd, -.1 * cd);
//                stack.rotate(new Quaternion(0, 0, 15 * cd * sideFlip, true));

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

