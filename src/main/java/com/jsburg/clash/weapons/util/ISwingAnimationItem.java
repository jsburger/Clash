package com.jsburg.clash.weapons.util;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface ISwingAnimationItem {

    AnimType hasThirdPersonAnim(PlayerEntity player, ItemStack stack);

    default <T extends LivingEntity> void doThirdPersonAnim(PlayerEntity player, BipedModel<T> model, ItemStack itemStack, boolean leftHanded) {}

    enum AnimType {
        OVERWRITES,
        TRUE,
        FALSE
    }

}
