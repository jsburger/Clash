package com.jsburg.clash.weapons;

import com.jsburg.clash.enchantments.spear.DashEnchantment;
import com.jsburg.clash.registry.AllSounds;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.IPoseItem;
import com.jsburg.clash.weapons.util.ISpearAnimation;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class JumpRodItem extends WeaponItem implements IPoseItem, ISpearAnimation {
    private static final int maxCharge = 18;
    private static final int minCharge = 5;
    public JumpRodItem(float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    public int getUseDuration(ItemStack stack) {
        return 720000;
    }
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    protected List<Enchantment> vanillaEnchantments() {
        return Arrays.asList(Enchantments.KNOCKBACK, Enchantments.FIRE_ASPECT);
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            int chargeTime = getUseDuration(stack) - timeLeft;
            ItemStack rod = player.getActiveItemStack();

            float chargePercent = Math.min((float) chargeTime / maxCharge, 1);
            boolean doThrust = !(player.isSneaking()) && chargeTime > (minCharge) && player.isOnGround() && !player.isSwimming();

            if (doThrust) {
                player.addStat(Stats.ITEM_USED.get(this));
                player.swingArm(player.getActiveHand());
                AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_WHOOSH.get(), 0.3f, 1.0f);

                double boostedPercentage = Math.min(1, chargePercent * 1.4);
                Vector3d dir = player.getLookVec().scale(/*Math.sqrt(thrustLevel) * */2 * boostedPercentage);
                player.addVelocity(dir.x, dir.y / 2 + 0.2, dir.z);
                AttackHelper.damageItem(1, rod, player, player.getActiveHand());

                player.addExhaustion(0.1f);
            }
        }
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        DashEnchantment.tryAgilityDash(worldIn, playerIn, stack);

        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(stack);
    }


    @Override
    public boolean hasPose(PlayerEntity player, ItemStack stack, boolean isActive) {
        return isActive;
    }

    @Override
    public <T extends LivingEntity> void doPose(PlayerEntity player, BipedModel<T> model, ItemStack itemStack, boolean leftHanded, boolean isActive) {
        ModelRenderer spearArm = leftHanded ? model.bipedLeftArm : model.bipedRightArm;
//        ModelRenderer otherArm = leftHanded ? model.bipedRightArm : model.bipedLeftArm;
        int sideFlip = leftHanded ? -1 : 1;

        spearArm.rotateAngleX *= .4f;
        spearArm.rotateAngleX += .4f;
        spearArm.rotateAngleY += .3f * sideFlip;
        spearArm.rotateAngleZ += .3f * sideFlip;
        spearArm.rotationPointX += 1f * sideFlip;
        spearArm.rotationPointY += 4f;
        spearArm.rotationPointZ += 2f;

//        otherArm.rotateAngleX *= .4f;
//        otherArm.rotateAngleX -= .4f;
//        otherArm.rotateAngleZ += .9f * sideFlip;
//        otherArm.rotationPointX -= 2f * sideFlip;
//        otherArm.rotationPointY -= 2f;
//        otherArm.rotationPointZ -= 3f;

        model.bipedBody.rotateAngleY += .3f * sideFlip;
    }

    @Override
    public int getMaxCharge(ItemStack stack) {
        return maxCharge;
    }
}
