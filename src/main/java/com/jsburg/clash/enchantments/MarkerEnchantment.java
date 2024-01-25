package com.jsburg.clash.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Simple enchantment that has one level, only exists to be checked for in other parts of code.
 */
public class MarkerEnchantment extends ClashEnchantment {
    public final Boolean isTreasure;
    public final Boolean isTradeable;
    public final int minLevel;
    public final int maxLevel;

    public MarkerEnchantment(boolean isTreasure, boolean isTradeable, Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
        this.isTreasure = isTreasure;
        this.isTradeable = isTradeable;
        minLevel = 50;
        maxLevel = 60;
    }
    public MarkerEnchantment(int minEnchantLevel, int maxEnchantLevel, Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
        this.minLevel = minEnchantLevel;
        this.maxLevel = maxEnchantLevel;
        this.isTreasure = false;
        this.isTradeable = true;
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return minLevel;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return maxLevel;
    }

    @Override
    public boolean isTradeable() {
        return isTradeable;
    }

    @Override
    public boolean isTreasureOnly() {
        return isTreasure;
    }


}
