package com.jsburg.clash.mixin;

import com.jsburg.clash.weapons.util.IThirdPersonRenderHook;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> {

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "renderItem"), remap = false, cancellable = true)
    public void clashOnRenderArmItem(AbstractClientPlayer entity, float p_109373_, float p_109374_, InteractionHand p_109375_, float p_109376_, ItemStack itemStack, float p_109378_, PoseStack p_109379_, MultiBufferSource p_109380_, int p_109381_, CallbackInfo ci) {
        if (itemStack.getItem() instanceof IThirdPersonRenderHook) {
            IThirdPersonRenderHook item = (IThirdPersonRenderHook) itemStack.getItem();
            if (item.onThirdPersonRender(((ItemInHandRenderer<T, M>)(Object)this).getEntityModel(), entity, itemStack, transformType, side, poseStack, renderBuffer, light)) {
                ci.cancel();
            }
        }

    }

}
