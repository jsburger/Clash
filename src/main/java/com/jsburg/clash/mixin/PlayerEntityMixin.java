package com.jsburg.clash.mixin;

import com.jsburg.clash.weapons.util.IHitListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    @Inject(method = "attackTargetEntityWithCurrentItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hitEntity(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/player/PlayerEntity;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void clash$AttackWithItemMixin(Entity target, CallbackInfo CI,
                                                  float f, float f1, float f2) {

        ItemStack heldItem = getHeldItemMainhand();
        if (heldItem.getItem() instanceof IHitListener) {
            ((IHitListener)heldItem.getItem()).onHit(heldItem, (LivingEntity) target, f2 > .9f);
        }

    }

}
