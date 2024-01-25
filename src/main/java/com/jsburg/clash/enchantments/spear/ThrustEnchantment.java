package com.jsburg.clash.enchantments.spear;

import com.jsburg.clash.enchantments.ClashEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ThrustEnchantment extends ClashEnchantment {

    public ThrustEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    public int getMinCost(int enchantmentLevel) {
        return 8 + (enchantmentLevel - 1) * 12;
    }

    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 24;
    }

}
