package com.jsburg.clash.weapons;

import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class BillhookItem extends SpearItem {

    public BillhookItem(int attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        TextComponent text = new TextComponent(" ");
        text.append(new TranslatableComponent("item.clash.billhook.inverted_knockback"));
        tooltip.add(text.withStyle(ChatFormatting.DARK_GREEN));
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
    protected void onStabHit(ItemStack stack, Player player, LivingEntity target, float chargePercent) {
        super.onStabHit(stack, player, target, chargePercent);
        Vec3 motion = target.getDeltaMovement();
        Vec3 diff = AttackHelper.getEntityPosition(player).subtract(AttackHelper.getEntityPosition(target));
        Vec3 newMotion = new Vec3(diff.x(), 0, diff.z());
        newMotion = newMotion.normalize().scale(new Vec3(motion.x(), 0, motion.z()).length());
        target.setDeltaMovement(newMotion.x(), motion.y(), newMotion.z());
    }

    @Override
    protected boolean canStabCrit(ItemStack stack) {
        return super.canStabCrit(stack);
    }
}
