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
    private <T extends LivingEntity> void onFirstHandPose(T entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            BipedModel<T> model = (BipedModel<T>) (Object) this;
            PlayerEntity player = (PlayerEntity) entity;

            Set<Hand> hands = ImmutableSet.of(Hand.MAIN_HAND, Hand.OFF_HAND);
            for (Hand hand : hands) {
                boolean active = player.isHandActive() && hand == player.getActiveHand();
                ItemStack handItem = player.getHeldItem(hand);
                Item itemItem = handItem.getItem();
                if (itemItem instanceof IPoseItem) {
                    if (((IPoseItem) itemItem).hasPose(player, handItem, active)) {
                        boolean leftHanded = (hand == Hand.OFF_HAND ^ player.getPrimaryHand() == HandSide.LEFT);
                        ((IPoseItem) itemItem).doPose(player, model, handItem, leftHanded, active);
                        //Prevents other item from doing any posing at all. might be cool to replace with a per hand basis
                        ci.cancel();
                        break;
                    }
                }
            }
        }
    }

    @Inject(method = "func_241654_b_", at = @At("HEAD"), remap = false, cancellable = true)
    private <T extends LivingEntity> void onSecondHandPose(T entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) entity;
            Set<Hand> hands = ImmutableSet.of(Hand.MAIN_HAND, Hand.OFF_HAND);
            for (Hand hand : hands) {
                boolean active = player.isHandActive() && hand == player.getActiveHand();
                ItemStack handItem = player.getHeldItem(hand);
                Item itemItem = handItem.getItem();
                if (itemItem instanceof IPoseItem) {
                    if (((IPoseItem) itemItem).hasPose(player, handItem, active)) {
                        //Prevents other item from doing any posing at all. might be cool to replace with a per hand basis
                        ci.cancel();
                        break;
                    }
                }
            }
        }
    }

}
