package com.jsburg.clash.weapons.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface IHitListener {
    void onHit(ItemStack stack, LivingEntity target, boolean isCharged);

}
