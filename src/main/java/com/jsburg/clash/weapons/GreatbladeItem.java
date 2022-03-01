package com.jsburg.clash.weapons;

import com.jsburg.clash.Clash;
import com.jsburg.clash.entity.GreatbladeSlashEntity;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class GreatbladeItem extends WeaponItem {

    public GreatbladeItem(float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    public int getUseDuration(ItemStack stack) {
        return 720000;
    }

    public int getMaxCharge() {
        return 20;
    }

    public static int swingTimeMax() {
        return 10;
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return super.canContinueUsing(oldStack, newStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (getSwingTime(stack) > 0) {
            addSwingTime(stack, -1);
        }
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
            }

            setSwingTime(stack, swingTimeMax() + 1);
            player.resetCooldown();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(stack);
    }

    public static int getSwingTime(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        return nbt != null ? nbt.getInt("SwingTime") : 0;
    }

    public static void setSwingTime(ItemStack stack, int time) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt("SwingTime", time);
    }

    private static void addSwingTime(ItemStack stack, int add) {
        setSwingTime(stack, getSwingTime(stack) + add);
    }

    //See PlayerEntityMixin
    public interface IGreatbladeUser {
        int getSwingTime();
        void resetSwingTime();
        void incrementSwingTime();
    }
}
