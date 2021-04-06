package com.jsburg.clash.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class TipperEnchantment extends Enchantment {

    public TipperEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
        super(rarityIn, typeIn, slots);
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 25;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return 50;
    }


}
