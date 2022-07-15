package com.jsburg.clash.weapons;

import com.jsburg.clash.Clash;
import com.jsburg.clash.entity.GreatbladeSlashEntity;
import com.jsburg.clash.util.ItemAnimator;
import com.jsburg.clash.util.TextHelper;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GreatbladeItem extends WeaponItem {

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

            if (chargeTime >= getMaxCharge()) {
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

    //See PlayerEntityMixin
    public interface IGreatbladeUser {
        int getSwingTime();
        void resetSwingTime();
        void incrementSwingTime();
    }

    public static class GreatbladeAnimation extends ItemAnimator.SimpleItemAnimation {
        public GreatbladeAnimation() {
            super(swingTimeMax());
        }
    }
}
