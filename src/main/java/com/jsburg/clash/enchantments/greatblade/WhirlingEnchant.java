package com.jsburg.clash.enchantments.greatblade;

import com.jsburg.clash.enchantments.ClashEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class WhirlingEnchant extends ClashEnchantment {
    public WhirlingEnchant(Rarity rarity, EnchantmentCategory enchantmentType, EquipmentSlot... equipmentSlotTypes) {
        super(rarity, enchantmentType, equipmentSlotTypes);
    }
    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinCost(int level) {
        return (level) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }
}
