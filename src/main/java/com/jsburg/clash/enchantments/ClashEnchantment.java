package com.jsburg.clash.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class ClashEnchantment extends Enchantment {
    public ClashEnchantment(Rarity rarity, EnchantmentCategory enchantmentType, EquipmentSlot... equipmentSlotTypes) {
        super(rarity, enchantmentType, equipmentSlotTypes);
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }
}
