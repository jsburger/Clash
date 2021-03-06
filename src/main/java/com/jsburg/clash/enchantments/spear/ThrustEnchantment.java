package com.jsburg.clash.enchantments.spear;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class ThrustEnchantment extends Enchantment {

    public ThrustEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 8 + (enchantmentLevel - 1) * 12;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 24;
    }

}
