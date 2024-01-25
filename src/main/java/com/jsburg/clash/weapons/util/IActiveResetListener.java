package com.jsburg.clash.weapons.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IActiveResetListener {
    void onHandReset(ItemStack stack, LivingEntity entity);
}
