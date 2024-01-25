package com.jsburg.clash.weapons.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IHitListener {
    void onHit(ItemStack stack, LivingEntity target, boolean isCharged);

}
