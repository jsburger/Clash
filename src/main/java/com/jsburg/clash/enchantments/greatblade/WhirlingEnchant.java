package com.jsburg.clash.enchantments.greatblade;

import com.jsburg.clash.enchantments.ClashEnchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class WhirlingEnchant extends ClashEnchantment {
    public WhirlingEnchant(Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType... equipmentSlotTypes) {
        super(rarity, enchantmentType, equipmentSlotTypes);
    }
    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinEnchantability(int level) {
        return (level) * 10;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return getMinEnchantability(level) + 20;
    }
}
