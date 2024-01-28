package com.jsburg.clash.weapons;

import com.jsburg.clash.enchantments.spear.DashEnchantment;
import com.jsburg.clash.registry.AllSounds;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.ISpearAnimation;
import com.jsburg.clash.weapons.util.IThirdPersonArmController;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

public class JumpRodItem extends WeaponItem implements IThirdPersonArmController, ISpearAnimation {
    private static final int maxCharge = 18;
    private static final int minCharge = 5;
    public JumpRodItem(float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    public int getUseDuration(ItemStack stack) {
        return 720000;
    }

    @Override
    protected List<Enchantment> vanillaEnchantments() {
        return Arrays.asList(Enchantments.KNOCKBACK, Enchantments.FIRE_ASPECT);
    }

    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player) {
            Player player = (Player) entityLiving;
            int chargeTime = getUseDuration(stack) - timeLeft;
            ItemStack rod = player.getUseItem();

            float chargePercent = Math.min((float) chargeTime / maxCharge, 1);
            boolean doThrust = !(player.isShiftKeyDown()) && chargeTime > (minCharge) && player.onGround() && !player.isSwimming();

            if (doThrust) {
                player.awardStat(Stats.ITEM_USED.get(this));
                player.swing(player.getUsedItemHand());
                AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_WHOOSH.get(), 0.3f, 1.0f);

                double boostedPercentage = Math.min(1, chargePercent * 1.4);
                Vec3 dir = player.getLookAngle().scale(/*Math.sqrt(thrustLevel) * */2 * boostedPercentage);
                player.push(dir.x, dir.y / 2 + 0.2, dir.z);
                AttackHelper.damageItem(1, rod, player, player.getUsedItemHand());

                player.causeFoodExhaustion(0.1f);
            }
        }
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        DashEnchantment.tryAgilityDash(worldIn, playerIn, stack);

        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(stack);
    }


    @Override
    public AnimType hasThirdPersonAnim(Player player, ItemStack stack, boolean isActive, InteractionHand hand) {
        return AnimType.ifTrue(isActive);
    }

    @Override
    public <T extends LivingEntity> void doThirdPersonAnim(Player player, HumanoidModel<T> model, ItemStack itemStack, float partialTicks, boolean leftHanded, boolean isActive, InteractionHand hand) {
        ModelPart spearArm = leftHanded ? model.leftArm : model.rightArm;
//        ModelRenderer otherArm = leftHanded ? model.bipedRightArm : model.bipedLeftArm;
        int sideFlip = leftHanded ? -1 : 1;

        spearArm.xRot *= .4f;
        spearArm.xRot += .4f;
        spearArm.yRot += .3f * sideFlip;
        spearArm.zRot += .3f * sideFlip;
        spearArm.x += 1f * sideFlip;
        spearArm.y += 4f;
        spearArm.z += 2f;

//        otherArm.rotateAngleX *= .4f;
//        otherArm.rotateAngleX -= .4f;
//        otherArm.rotateAngleZ += .9f * sideFlip;
//        otherArm.rotationPointX -= 2f * sideFlip;
//        otherArm.rotationPointY -= 2f;
//        otherArm.rotationPointZ -= 3f;

        model.body.yRot += .3f * sideFlip;
    }

    @Override
    public int getMaxCharge(ItemStack stack) {
        return maxCharge;
    }
}
