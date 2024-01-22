package com.jsburg.clash.enchantments.greatblade;

import com.jsburg.clash.enchantments.ClashEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class ThrumEnchantment extends ClashEnchantment {
    public ThrumEnchantment(Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType... equipmentSlotTypes) {
        super(rarity, enchantmentType, equipmentSlotTypes);
    }
    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinEnchantability(int level) {
        return (level - 1) * 7;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return getMinEnchantability(level) + 9;
    }

    @Override
    protected boolean canApplyTogether(Enchantment enchantment) {
        if (enchantment instanceof CrushingEnchantment) return false;
        return super.canApplyTogether(enchantment);
    }
}
