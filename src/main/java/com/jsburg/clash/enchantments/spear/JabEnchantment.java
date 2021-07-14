package com.jsburg.clash.enchantments.spear;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.ITag;

public class JabEnchantment extends Enchantment {

    public JabEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
        super(rarityIn, typeIn, slots);
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 40;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return 50;
    }

    @Override
    protected boolean canApplyTogether(Enchantment ench) {
        if (ench instanceof ThrustEnchantment) return false;
        return super.canApplyTogether(ench);
    }

    @Override
    public boolean canVillagerTrade() {
        return false;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }
}
