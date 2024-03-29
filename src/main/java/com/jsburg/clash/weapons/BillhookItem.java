package com.jsburg.clash.weapons;

import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BillhookItem extends SpearItem {

    public BillhookItem(int attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        StringTextComponent text = new StringTextComponent(" ");
        text.appendSibling(new TranslationTextComponent("item.clash.billhook.inverted_knockback"));
        tooltip.add(text.mergeStyle(TextFormatting.DARK_GREEN));
    }

    @Override
    public int getMinCharge(ItemStack stack) {
        int spearMin = super.getMinCharge(stack);
        return Math.max(spearMin - 2, 0);
    }

    @Override
    public int getMaxCharge(ItemStack stack) {
        int spearMax = super.getMaxCharge(stack);
        return Math.max(spearMax - 5, 3);
    }

    @Override
    protected void onStabHit(ItemStack stack, PlayerEntity player, LivingEntity target, float chargePercent) {
        super.onStabHit(stack, player, target, chargePercent);
        Vector3d motion = target.getMotion();
        Vector3d diff = AttackHelper.getEntityPosition(player).subtract(AttackHelper.getEntityPosition(target));
        Vector3d newMotion = new Vector3d(diff.getX(), 0, diff.getZ());
        newMotion = newMotion.normalize().scale(new Vector3d(motion.getX(), 0, motion.getZ()).length());
        target.setMotion(newMotion.getX(), motion.getY(), newMotion.getZ());
    }

    @Override
    protected boolean canStabCrit(ItemStack stack) {
        return super.canStabCrit(stack);
    }
}
