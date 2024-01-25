package com.jsburg.clash.weapons.util;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IThirdPersonArmController {

    AnimType hasThirdPersonAnim(Player player, ItemStack stack, boolean active, InteractionHand hand);

    <T extends LivingEntity> void doThirdPersonAnim(Player player, HumanoidModel<T> model, ItemStack itemStack, float partialTicks, boolean leftHanded, boolean isActive, InteractionHand hand);

    enum AnimType {
        OVERWRITES,
        TRUE,
        FALSE
    }

}
