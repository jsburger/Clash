package com.jsburg.clash.weapons.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface IActiveResetListener {
    void onHandReset(ItemStack stack, LivingEntity entity);
}
