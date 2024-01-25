package com.jsburg.clash.weapons.util;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;


public interface IPoseItem {

    boolean hasPose(PlayerEntity player, ItemStack stack, boolean isActive);

    default <T extends LivingEntity> void doPose(PlayerEntity player, BipedModel<T> model, ItemStack itemStack, boolean leftHanded, boolean isActive) {}

}
