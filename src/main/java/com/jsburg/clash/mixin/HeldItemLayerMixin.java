package com.jsburg.clash.mixin;

import com.jsburg.clash.weapons.util.IThirdPersonRenderHook;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemLayer.class)
public class HeldItemLayerMixin <T extends LivingEntity, M extends EntityModel<T>&IHasArm> {

    @Inject(method = "func_229135_a_", at = @At(value = "INVOKE", target = "renderItemSide"), remap = false, cancellable = true)
    public void clashOnRenderArmItem(LivingEntity entity, ItemStack itemStack,
                              ItemCameraTransforms.TransformType transformType, HandSide side,
                              MatrixStack poseStack, IRenderTypeBuffer renderBuffer, int light, CallbackInfo ci) {
        if (itemStack.getItem() instanceof IThirdPersonRenderHook) {
            IThirdPersonRenderHook item = (IThirdPersonRenderHook) itemStack.getItem();
            if (item.onThirdPersonRender(((HeldItemLayer<T, M>)(Object)this).getEntityModel(), entity, itemStack, transformType, side, poseStack, renderBuffer, light)) {
                ci.cancel();
            }
        }

    }

}
