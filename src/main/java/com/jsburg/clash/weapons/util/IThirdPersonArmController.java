package com.jsburg.clash.weapons.util;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface IThirdPersonArmController {

    AnimType hasThirdPersonAnim(PlayerEntity player, ItemStack stack, boolean active, Hand hand);

    <T extends LivingEntity> void doThirdPersonAnim(PlayerEntity player, BipedModel<T> model, ItemStack itemStack, float partialTicks, boolean leftHanded, boolean isActive, Hand hand);

    enum AnimType {
        OVERWRITES,
        TRUE,
        FALSE
    }

}
