package com.jsburg.clash.enchantments.spear;

import com.jsburg.clash.enchantments.ClashEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FlurryEnchantment extends ClashEnchantment {

    public static final int MAX_LEVEL = 3;
    public static final float SPEED_PER_LEVEL = 0.2f;

    public FlurryEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    public int getMinCost(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 8;
    }

    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 12;
    }

}
