//package com.jsburg.clash.mixin;
//
//import com.jsburg.clash.weapons.util.IActiveResetListener;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.item.ItemStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(LivingEntity.class)
//public abstract class LivingEntityMixin {
//    @Shadow public abstract ItemStack getActiveItemStack();
//
//    @Inject(method = "resetActiveHand",
//        at = @At(value = "HEAD")
//    )
//    private void clash$onResetActiveHand(CallbackInfo CI) {
//        ItemStack stack = this.getActiveItemStack();
//        if (stack != null && !stack.isEmpty()) {
//            if (stack.getItem() instanceof IActiveResetListener) {
//                ((IActiveResetListener) stack.getItem()).onHandReset(stack, (LivingEntity) (Object) this);
//            }
//        }
//    }
//}
