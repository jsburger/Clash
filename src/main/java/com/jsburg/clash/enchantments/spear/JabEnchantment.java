package com.jsburg.clash.enchantments.spear;

import com.jsburg.clash.enchantments.ClashEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class JabEnchantment extends ClashEnchantment {

    public JabEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
    }

    public int getMinCost(int enchantmentLevel) {
        return 40;
    }

    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        if (ench instanceof ThrustEnchantment) return false;
        return super.checkCompatibility(ench);
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
