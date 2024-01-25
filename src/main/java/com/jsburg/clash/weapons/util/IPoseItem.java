package com.jsburg.clash.weapons.util;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface IPoseItem {

    boolean hasPose(Player player, ItemStack stack, boolean isActive);

    default <T extends LivingEntity> void doPose(Player player, HumanoidModel<T> model, ItemStack itemStack, boolean leftHanded, boolean isActive) {}

}
