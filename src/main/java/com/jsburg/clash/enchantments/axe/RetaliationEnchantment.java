package com.jsburg.clash.enchantments.axe;

import com.jsburg.clash.enchantments.ClashEnchantment;
import com.jsburg.clash.registry.AllEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class RetaliationEnchantment extends ClashEnchantment {
    public RetaliationEnchantment(Enchantment.Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 2 + (enchantmentLevel - 1) * 6;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 8;
    }

    //Called in LivingEvents
    public static void onUserHurt(LivingEntity user, int level) {
        MobEffectInstance retaliation = user.getEffect(AllEffects.RETALIATION.get());
        int amp = retaliation == null ? -1 : retaliation.getAmplifier();
        user.addEffect(new MobEffectInstance(AllEffects.RETALIATION.get(), 20 * 20, Mth.clamp(amp + 1, 0, level), false, true));
    }

}
