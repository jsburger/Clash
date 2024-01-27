package com.jsburg.clash.mixin;

import com.google.common.collect.ImmutableSet;
import com.jsburg.clash.weapons.util.IThirdPersonArmController;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    private static final Set<InteractionHand> hands = ImmutableSet.of(InteractionHand.MAIN_HAND, InteractionHand.OFF_HAND);

    @Inject(method = "setupAttackAnimation", at = @At("HEAD"), remap = false, cancellable = true)
    private <T extends LivingEntity> void onArmSwing(T entity, float ageInTicks, CallbackInfo ci) {
        if (entity instanceof Player player) {

            for (InteractionHand hand : hands) {
                boolean active = player.isUsingItem() && hand == player.getUsedItemHand();
                ItemStack handItem = player.getItemInHand(hand);
                Item item = handItem.getItem();
                if (item instanceof IThirdPersonArmController) {
                    IThirdPersonArmController.AnimType type = ((IThirdPersonArmController) item).hasThirdPersonAnim(player, handItem, active, hand);
                    if (type != IThirdPersonArmController.AnimType.FALSE) {
                        boolean leftHanded = (hand == InteractionHand.OFF_HAND ^ player.getMainArm() == HumanoidArm.LEFT);
                        float partialTicks = (ageInTicks % 1);
                        ((IThirdPersonArmController) item).doThirdPersonAnim(player, (HumanoidModel<T>) (Object) this, handItem, partialTicks, leftHanded, active, hand);
                        if (type == IThirdPersonArmController.AnimType.OVERWRITES)
                            ci.cancel();
                        break;
                    }
                }
            }
        }
    }


}
