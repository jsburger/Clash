package com.jsburg.clash.weapons;

import com.jsburg.clash.entity.GreatbladeSlashEntity;
import com.jsburg.clash.util.ItemAnimator;
import com.jsburg.clash.util.TextHelper;
import com.jsburg.clash.weapons.util.IThirdPersonArmController;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GreatbladeItem extends WeaponItem implements IThirdPersonArmController {

    public GreatbladeItem(float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add((new TranslationTextComponent("item.clash.spear.when_charged")).mergeStyle(TextFormatting.GRAY));
        tooltip.add(TextHelper.getBonusText("item.clash.lost_greatblade.bonus_damage", 5));
    }


    public int getUseDuration(ItemStack stack) {
        return 720000;
    }

    public int getMaxCharge() {
        return 20;
    }

    public static int swingTimeMax() {
        return 7;
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return super.canContinueUsing(oldStack, newStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            int chargeTime = getUseDuration(stack) - timeLeft;

            ItemStack sword = player.getActiveItemStack();

            if (chargeTime >= getMaxCharge() || true) {
                GreatbladeSlashEntity slash = new GreatbladeSlashEntity(worldIn, sword, player.getPositionVec(), player);
                slash.setMotion(player.getLook(0).scale(1));
                worldIn.addEntity(slash);

                if (worldIn.isRemote) {
                    ItemAnimator.startAnimation(player, sword, player.getActiveHand(), new GreatbladeAnimation());
                }

                player.resetCooldown();
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(stack);
    }

    @Override
    public AnimType hasThirdPersonAnim(PlayerEntity player, ItemStack stack, boolean isActive, Hand hand) {
        if (isActive) return AnimType.OVERWRITES;
        ItemAnimator.ItemAnimation animation = ItemAnimator.getAnimation(GreatbladeAnimation.class, player, hand);
        if (animation != null) return AnimType.OVERWRITES;
        return AnimType.FALSE;
    }

    @Override
    public <T extends LivingEntity> void doThirdPersonAnim(PlayerEntity player, BipedModel<T> model, ItemStack itemStack, float partialTicks, boolean leftHanded, boolean isActive, Hand hand) {
        ModelRenderer swordArm = leftHanded ? model.bipedLeftArm : model.bipedRightArm;
        ModelRenderer otherArm = leftHanded ? model.bipedRightArm : model.bipedLeftArm;
        int sideFlip = leftHanded ? -1 : 1;
        ItemAnimator.ItemAnimation animation = ItemAnimator.getAnimation(GreatbladeAnimation.class, player, hand);
        float swingProgress = 0;
        if (animation != null) swingProgress = animation.getProgress(partialTicks);

        float chargeLerp = 1 - swingProgress;
        //Charging pose, acts as the base for the swing too
        swordArm.rotateAngleX *= .4f * chargeLerp;
        swordArm.rotateAngleX -= .4f * chargeLerp;
        swordArm.rotateAngleY -= .3f * sideFlip * chargeLerp;
        swordArm.rotateAngleZ += .3f * sideFlip * chargeLerp;
        swordArm.rotationPointX += 1f * sideFlip * chargeLerp;
        swordArm.rotationPointY += 4f * chargeLerp;
        swordArm.rotationPointZ += 2f * chargeLerp;

        otherArm.rotateAngleX *= .4f * chargeLerp;
        otherArm.rotateAngleX -= .4f * chargeLerp;
        otherArm.rotateAngleZ += .9f * sideFlip * chargeLerp;
        otherArm.rotationPointX -= 2f * sideFlip * chargeLerp;
        otherArm.rotationPointY -= 2f * chargeLerp;
        otherArm.rotationPointZ -= 3f * chargeLerp;

        model.bipedBody.rotateAngleY += .3f * sideFlip * chargeLerp;

        //Swing animation
        if (animation != null) {
            float pi = (float) Math.PI;
            float swingPercent = (float)Math.sin(Math.pow(animation.getProgress(partialTicks), .6) * pi);
            float realSwingPercent = animation.getProgress(partialTicks);

            swordArm.rotateAngleX -= 1 * pi * swingPercent;
            swordArm.rotateAngleZ += .4 * pi * swingPercent * sideFlip;
            swordArm.rotateAngleY += .1 * pi * swingPercent * sideFlip;
            swordArm.rotationPointX += 2.5f * sideFlip * swingPercent;
            swordArm.rotationPointY -= 6f * swingPercent;
            swordArm.rotationPointZ -= 3f * swingPercent;

            otherArm.rotateAngleX -= .6 * pi * swingPercent;
            otherArm.rotateAngleZ += .4 * pi * swingPercent * sideFlip;
            otherArm.rotationPointX += 3f * sideFlip * swingPercent;
            otherArm.rotationPointY += 3f * swingPercent;
            otherArm.rotationPointZ += 4.5f * swingPercent;

            model.bipedBody.rotateAngleY -= .2f * pi * swingPercent * sideFlip;
        }

    }

    public static class GreatbladeAnimation extends ItemAnimator.SimpleItemAnimation {
        public GreatbladeAnimation() {
            super(swingTimeMax());
        }
    }
}
