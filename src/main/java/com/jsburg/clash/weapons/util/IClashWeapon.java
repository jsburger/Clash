package com.jsburg.clash.weapons.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IClashWeapon {

    default void onHit(ItemStack stack, PlayerEntity player, Entity target) {}

}
