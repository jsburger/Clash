package com.jsburg.clash.weapons.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface IThirdPersonRenderHook {
    //Return true to cancel the original rendering of the item.
    <T extends LivingEntity, M extends EntityModel<T> & ArmedModel> boolean onThirdPersonRender(M model, LivingEntity entity, ItemStack itemStack,
                                                                                                ItemDisplayContext context, HumanoidArm side,
                                                                                                PoseStack poseStack, MultiBufferSource renderBuffer, int light);
}
