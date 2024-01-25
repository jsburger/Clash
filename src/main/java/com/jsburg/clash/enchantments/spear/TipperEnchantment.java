package com.jsburg.clash.enchantments.spear;

import com.jsburg.clash.enchantments.ClashEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TipperEnchantment extends ClashEnchantment {

    public TipperEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
    }

    public int getMinCost(int enchantmentLevel) {
        return 25;
    }

    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }


}
