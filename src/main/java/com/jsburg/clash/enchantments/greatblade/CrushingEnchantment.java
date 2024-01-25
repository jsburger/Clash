package com.jsburg.clash.enchantments.greatblade;

import com.jsburg.clash.enchantments.ClashEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class CrushingEnchantment extends ClashEnchantment {
    public CrushingEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinCost(int level) {
        return (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 15;
    }
}
