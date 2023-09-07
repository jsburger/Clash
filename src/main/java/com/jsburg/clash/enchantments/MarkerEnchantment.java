package com.jsburg.clash.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

/**
 * Simple enchantment that has one level, only exists to be checked for in other parts of code.
 */
public class MarkerEnchantment extends Enchantment {
    public final Boolean isTreasure;
    public final Boolean isTradeable;
    public final int minLevel;
    public final int maxLevel;

    public MarkerEnchantment(boolean isTreasure, boolean isTradeable, Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
        super(rarityIn, typeIn, slots);
        this.isTreasure = isTreasure;
        this.isTradeable = isTradeable;
        minLevel = 50;
        maxLevel = 60;
    }
    public MarkerEnchantment(int minEnchantLevel, int maxEnchantLevel, Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
        super(rarityIn, typeIn, slots);
        this.minLevel = minEnchantLevel;
        this.maxLevel = maxEnchantLevel;
        this.isTreasure = false;
        this.isTradeable = true;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return minLevel;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return maxLevel;
    }

    @Override
    public boolean canVillagerTrade() {
        return isTradeable;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return isTreasure;
    }


}
