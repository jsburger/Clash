package com.jsburg.clash.enchantments.axe;

import com.jsburg.clash.enchantments.ClashEnchantment;
import com.jsburg.clash.registry.AllEffects;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;

public class RampageEnchantment extends ClashEnchantment {

    public RampageEnchantment(Rarity rarityIn, EnchantmentType typeIn,  EquipmentSlotType... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 10 + (enchantmentLevel - 1) * 8;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 8;
    }

    //Called in LivingEvents
    public static void onKill(LivingEntity user, int level) {
        EffectInstance rampageStacks = user.getActivePotionEffect(AllEffects.RAMPAGING.get());
        int amp = rampageStacks == null ? -1 : rampageStacks.getAmplifier();
        user.addPotionEffect(new EffectInstance(AllEffects.RAMPAGING.get(), 30 + 20 * level, amp + 1, false, false));
    }

}
