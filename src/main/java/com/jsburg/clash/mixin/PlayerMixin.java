package com.jsburg.clash.mixin;

import com.jsburg.clash.weapons.util.IHitListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> p_i48577_1_, Level p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    @Inject(method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtEnemy(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/player/Player;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void clash$AttackWithItemMixin(Entity target, CallbackInfo CI,
                                           float f, float f1, float f2) {

        ItemStack heldItem = getMainHandItem();
        if (heldItem.getItem() instanceof IHitListener) {
            ((IHitListener)heldItem.getItem()).onHit(heldItem, (LivingEntity) target, f2 > .9f);
        }

    }

}
