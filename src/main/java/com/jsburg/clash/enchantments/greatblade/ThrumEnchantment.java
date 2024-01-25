package com.jsburg.clash.enchantments.greatblade;

import com.jsburg.clash.enchantments.ClashEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ThrumEnchantment extends ClashEnchantment {
    public ThrumEnchantment(Rarity rarity, EnchantmentCategory enchantmentType, EquipmentSlot... equipmentSlotTypes) {
        super(rarity, enchantmentType, equipmentSlotTypes);
    }
    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinCost(int level) {
        return (level - 1) * 7;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 9;
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        if (enchantment instanceof CrushingEnchantment) return false;
        return super.checkCompatibility(enchantment);
    }
}
