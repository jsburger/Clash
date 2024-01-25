package com.jsburg.clash.enchantments.axe;

import com.jsburg.clash.enchantments.ClashEnchantment;
import com.jsburg.clash.registry.AllEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class RampageEnchantment extends ClashEnchantment {

    public RampageEnchantment(Rarity rarityIn, EnchantmentCategory typeIn,  EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 10 + (enchantmentLevel - 1) * 8;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 8;
    }

    //Called in LivingEvents
    public static void onKill(LivingEntity user, int level) {
        MobEffectInstance rampageStacks = user.getEffect(AllEffects.RAMPAGING.get());
        int amp = rampageStacks == null ? -1 : rampageStacks.getAmplifier();
        user.addEffect(new MobEffectInstance(AllEffects.RAMPAGING.get(), 30 + 20 * level, amp + 1, false, false));
    }

}
