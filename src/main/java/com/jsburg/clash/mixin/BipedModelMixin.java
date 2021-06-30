package com.jsburg.clash.mixin;

import com.google.common.collect.ImmutableSet;
import com.jsburg.clash.weapons.util.IPoseItem;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(BipedModel.class)
public class BipedModelMixin {

    @Inject(method = "func_241655_c_", at = @At("HEAD"), remap = false, cancellable = true)
    private <T extends LivingEntity> void onMainHandPose(T entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            BipedModel<T> model = (BipedModel<T>) (Object) this;
            PlayerEntity player = (PlayerEntity) entity;
//            ItemStack stack = player.getActiveItemStack();
//            Item item = stack.getItem();
//            if (item instanceof IPoseItem && ((IPoseItem) item).hasActivePose()) {
//                ((IPoseItem) item).doPose(player, model, stack, player.getActiveHand() == Hand.OFF_HAND ^ player.getPrimaryHand() == HandSide.LEFT);
//                doPose(player, model, item, player.getActiveHand() == Hand.MAIN_HAND);
//                ci.cancel();
//            }

            Set<Hand> hands = ImmutableSet.of(Hand.MAIN_HAND, Hand.OFF_HAND);
            for (Hand hand : hands) {
                boolean active = player.isHandActive() && hand == player.getActiveHand();
                ItemStack handItem = player.getHeldItem(hand);
                Item itemItem = handItem.getItem();
                if (itemItem instanceof IPoseItem) {
                    if (((IPoseItem) itemItem).hasPose(player, handItem, active)) {
                        boolean leftHanded = (hand == Hand.OFF_HAND ^ player.getPrimaryHand() == HandSide.LEFT);
                        ((IPoseItem) itemItem).doPose(player, model, handItem, leftHanded, active);
                        ci.cancel();
                        break;
                    }
                }
            }
        }
    }

    @Inject(method = "func_241654_b_", at = @At("HEAD"), remap = false, cancellable = true)
    private <T extends LivingEntity> void onOffHandPose(T entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) entity;
            Set<Hand> hands = ImmutableSet.of(Hand.MAIN_HAND, Hand.OFF_HAND);
            for (Hand hand : hands) {
                boolean active = player.isHandActive() && hand == player.getActiveHand();
                ItemStack handItem = player.getHeldItem(hand);
                Item itemItem = handItem.getItem();
                if (itemItem instanceof IPoseItem) {
                    if (((IPoseItem) itemItem).hasPose(player, handItem, active)) {
                        ci.cancel();
                        break;
                    }
                }
            }


//            Item item = entity.getActiveItemStack().getItem();
//            if (item instanceof IPoseItem) {
//                if (((IPoseItem) item).hasActivePose())
//                    ci.cancel();
//            }
        }
    }

}
