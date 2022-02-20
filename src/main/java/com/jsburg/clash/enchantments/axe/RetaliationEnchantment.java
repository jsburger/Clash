package com.jsburg.clash.enchantments.axe;

import com.jsburg.clash.registry.AllEffects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;

public class RetaliationEnchantment extends Enchantment {
    public RetaliationEnchantment(Enchantment.Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 2 + (enchantmentLevel - 1) * 6;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 8;
    }

    //Called in LivingEvents
    public static void onUserHurt(LivingEntity user, int level) {
        EffectInstance retaliation = user.getActivePotionEffect(AllEffects.RETALIATION.get());
        int amp = retaliation == null ? -1 : retaliation.getAmplifier();
        user.addPotionEffect(new EffectInstance(AllEffects.RETALIATION.get(), 20 * 20, MathHelper.clamp(amp + 1, 0, level), false, false));
    }

}
