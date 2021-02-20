package com.jsburg.clash.weapons.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public interface IClashWeapon {

    default void onHit(ItemStack stack, PlayerEntity player, Entity target) {}

    default List<Enchantment> vanillaEnchantments() {
        return null;
    }
}
