package com.jsburg.clash.mixin;

import com.jsburg.clash.Clash;
import com.jsburg.clash.weapons.SpearItem;
import com.jsburg.clash.weapons.util.IClashWeapon;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BipedModel.class)
public class BipedModelMixin {

    @Inject(method = "func_241655_c_", at = @At("HEAD"), remap = false, cancellable = true)
    private <T extends LivingEntity> void onLeftArmPose(T entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            BipedModel<T> model = (BipedModel<T>) (Object) this;
            PlayerEntity player = (PlayerEntity) entity;
            ItemStack stack = player.getActiveItemStack();
            Item item = stack.getItem();
            if (item instanceof IClashWeapon && ((IClashWeapon) item).hasActivePose()) {
                ((IClashWeapon) item).doActivePose(player, model, stack, player.getActiveHand() == Hand.OFF_HAND ^ player.getPrimaryHand() == HandSide.LEFT);
//                doPose(player, model, item, player.getActiveHand() == Hand.MAIN_HAND);
                ci.cancel();
            }
        }
    }

    @Inject(method = "func_241654_b_", at = @At("HEAD"), remap = false, cancellable = true)
    private <T extends LivingEntity> void onRightArmPose(T entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            Item item = entity.getActiveItemStack().getItem();
            if (item instanceof IClashWeapon) {
                if (((IClashWeapon) item).hasActivePose())
                    ci.cancel();
            }
        }
    }

}
