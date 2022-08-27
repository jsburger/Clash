package com.jsburg.clash.weapons.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;

public interface IThirdPersonRenderHook {
    //Return true to cancel the original rendering of the item.
    <T extends LivingEntity, M extends EntityModel<T> & IHasArm> boolean onThirdPersonRender(M model, LivingEntity entity, ItemStack itemStack,
                                                                                             ItemCameraTransforms.TransformType transformType, HandSide side,
                                                                                             MatrixStack poseStack, IRenderTypeBuffer renderBuffer, int light);
}
