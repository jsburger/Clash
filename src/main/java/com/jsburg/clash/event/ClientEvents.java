package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.util.Easing;
import com.jsburg.clash.util.ItemAnimator;
import com.jsburg.clash.util.ScreenShaker;
import com.jsburg.clash.weapons.GreatbladeItem;
import com.jsburg.clash.weapons.SweptAxeItem;
import com.jsburg.clash.weapons.util.ISpearAnimation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.jsburg.clash.weapons.GreatbladeItem.hasExecutioner;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    private static boolean needsPop = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        AllParticles.registerParticleFactories();
    }

    //All the events below this are set up with listeners in Client setup

    public static void doClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ScreenShaker.tick();
            ItemAnimator.tick();
        }
    }

    public static void doCameraStuff(EntityViewRenderEvent.CameraSetup event) {
        ScreenShaker.applyScreenShake(event.getPartialTicks(), event);
        //Just resetting this between frames because if execution order *does* get weird I don't want stuff carrying over
        needsPop = false;
    }

    public static void fiddleWithHands(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        //Am I relying on execution order staying consistent? Yes.
        if (needsPop) {
            event.getPoseStack().popPose();
            needsPop = false;
        }
        boolean leftHanded = player.getMainArm() == HumanoidArm.LEFT ^ event.getHand() == InteractionHand.OFF_HAND;

        ItemAnimator.ItemAnimation animation = ItemAnimator.getAnimation(GreatbladeItem.GreatbladeAnimation.class, player, player.getUsedItemHand());
        float animationProgress = 0;
        if (animation != null) {
            animationProgress = animation.getProgress(event.getPartialTicks());
        }

        boolean sweptAxeDraw = (event.getHand() == InteractionHand.MAIN_HAND && event.getItemStack().getItem() instanceof SweptAxeItem && event.getEquipProgress() > .1);
        boolean greatbladeDraw = (event.getItemStack().getItem() instanceof GreatbladeItem && (
                (event.getHand() == player.getUsedItemHand() && player.isUsingItem()) ||
                animation != null)
            );

        if (event.getItemStack().getItem() instanceof ISpearAnimation) {

            boolean isActive = event.getHand() == player.getUsedItemHand() && player.isUsingItem();
            float cooldown = player.getCooldowns().getCooldownPercent(event.getItemStack().getItem(), event.getPartialTicks());
            boolean isCoolingDown = cooldown > 0 && !isActive && !player.swinging;
            boolean goingToMove = isActive || isCoolingDown;

            //Main hand always renders first, so it needs to remove its transformations.
            //If the offhand translates stuff it doesn't matter since its rendered last.
            if (goingToMove && event.getHand() == InteractionHand.MAIN_HAND) {
                event.getPoseStack().pushPose();
                needsPop = true;
            }
            int sideFlip = leftHanded ? -1 : 1;

            if (isActive) {
                Item spear = event.getItemStack().getItem();
                ISpearAnimation chargeGetter = (ISpearAnimation) spear;
                int useCount = player.getUseItemRemainingTicks();
                int useDuration = spear.getUseDuration(event.getItemStack());
                int useTime = useDuration - useCount;
                float chargePercent = Math.min((useTime + event.getPartialTicks()) / chargeGetter.getMaxCharge(event.getItemStack()), 1);


                float xAngle = -6 * chargePercent;
                float yAngle = 0 * chargePercent;
                float zAngle = 20 * chargePercent * sideFlip;
                event.getPoseStack().mulPose(new Quaternion(xAngle, yAngle, zAngle, true));

                double chargeOver = ((useTime + event.getPartialTicks()) - (chargeGetter.getMaxCharge(event.getItemStack()) - 4)) / 4;
                chargeOver = pow(Math.max(0, Math.min(1, chargeOver)), 2);
                if (chargeOver > 0) {
                    event.getPoseStack().translate(0, 0, .1 * chargeOver);
                }
            }

            if (isCoolingDown) {
                PoseStack stack = event.getPoseStack();
                float cd = (float) pow(cooldown, 3);

                stack.mulPose(new Quaternion(-10 * cd, 4 * cd, 0, true));
                stack.translate(0, -.1 * cd, -.1 * cd);
//                stack.rotate(new Quaternion(0, 0, 15 * cd * sideFlip, true));

            }
        }
        //Here's where the code starts getting to the point of wanting a system
        if (sweptAxeDraw || greatbladeDraw) {
            float swingProgress = event.getSwingProgress();
            HumanoidArm side = player.getMainArm();
            PoseStack stack = event.getPoseStack();
            int sideFlip = leftHanded ? -1 : 1;

            ItemInHandRenderer renderer = Minecraft.getInstance().getItemInHandRenderer();
            ItemTransforms.TransformType transform = leftHanded ? ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND : ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;

            float pi = (float)Math.PI;

            stack.pushPose();

            if (sweptAxeDraw) {
                float xOffset = -1.4F * Mth.sin((swingProgress) * pi);
                float yOffset = 0.2F * Mth.sin((float) (sqrt(swingProgress) * pi * 2F));
                float zOffset = -0.6F * Mth.sin((swingProgress) * pi * 2);
                stack.translate(sideFlip * xOffset, yOffset, zOffset);

                transformSideFirstPerson(stack, side, (swingProgress == 0 || swingProgress > .7 || event.getEquipProgress() == 0) ? event.getEquipProgress() : 0);
                stack.mulPose(Vector3f.ZP.rotationDegrees(sideFlip * -80 * Mth.sin((float) (sqrt(swingProgress) * pi))));
                stack.mulPose(Vector3f.XP.rotationDegrees(-180 * Mth.sin(swingProgress * pi)));
            }

            if (greatbladeDraw) {
                GreatbladeItem sword = (GreatbladeItem) event.getItemStack().getItem();
                int useCount = player.getUseItemRemainingTicks();
                int useDuration = sword.getUseDuration(event.getItemStack());
                int useTime = useDuration - useCount;
                float chargePercent = Math.min((useTime + event.getPartialTicks()) / (sword.getMaxCharge() + 1), 1);

                if (chargePercent > .8) {
                    double n = Math.sin((player.tickCount + event.getPartialTicks()) * 1.3) * .005 * chargePercent;
                    stack.translate(0, n, 0);
                }

                float swingPercent = animationProgress;

                if (swingPercent > 0) {
                    chargePercent = 1;
                }
                else swingPercent = 0;


                float chargeLerp = Mth.sin((float) (pow(chargePercent, 3) * pi/2)) - swingPercent;

                float swingEase = Easing.combine(Easing.Quart.out(swingPercent), swingPercent, .55f);
//                float swingEase = Easing.combine(Easing.Circ.inOut(swingPercent), swingPercent, .3f);
                float sineEase = Mth.sin(swingEase * pi);
                float fullSineEase = Mth.sin(swingEase * 2 * pi);

                boolean executioner = hasExecutioner(event.getItemStack());
                // Charging position
                stack.translate(1 * sideFlip * chargeLerp * (executioner ? .8f : 1f), -1.2 * chargeLerp * (executioner ? .65f : 1f), -.2 * chargeLerp);
                // Swinging position
                stack.translate(-2 * swingEase * sideFlip * (executioner ? .5f : 1f), .15 * sineEase - .1 * swingEase, 0 * sineEase);

                // Charge rotation
                stack.mulPose(Vector3f.YP.rotationDegrees(-10 * chargeLerp * sideFlip));
                stack.mulPose(Vector3f.ZP.rotationDegrees(140 * chargeLerp * sideFlip * (executioner ? 1.15f : 1f)));

                transformSideFirstPerson(stack, leftHanded ? HumanoidArm.LEFT : HumanoidArm.RIGHT, 0);
//                stack.rotate(Vector3f.YP.rotationDegrees(sideFlip * 50 * swingEase));

                // Swing animation
                Vector3f axis = new Vector3f(0, 0, 1);
                stack.mulPose(axis.rotationDegrees(90 * swingEase * sideFlip * (executioner ? 1.4f : 1f)));
                stack.mulPose(Vector3f.XP.rotationDegrees(210 * swingEase));
                stack.translate(.2 * sineEase * sideFlip, -1.2 * swingEase, 0);


            }

            renderer.renderItem(player, event.getItemStack(), transform, leftHanded, stack, event.getMultiBufferSource(), event.getPackedLight());
            stack.popPose();

            event.setCanceled(true);
        }

    }

    private static void transformFirstPerson(PoseStack matrixStackIn, HumanoidArm handIn, float swingProgress) {
        int i = handIn == HumanoidArm.RIGHT ? 1 : -1;
        float f = Mth.sin(swingProgress * swingProgress * (float)Math.PI);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float)i * (45.0F + f * -20.0F)));
        float f1 = Mth.sin((float) (sqrt(swingProgress) * (float)Math.PI));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((float)i * f1 * -20.0F));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(f1 * -80.0F));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float)i * -45.0F));
    }

    private static void transformSideFirstPerson(PoseStack matrixStackIn, HumanoidArm handIn, float equippedProg) {
        int i = handIn == HumanoidArm.RIGHT ? 1 : -1;
        matrixStackIn.translate(((float)i * 0.56F), (-0.52F + equippedProg * -0.6F), -0.72F);
    }

}

