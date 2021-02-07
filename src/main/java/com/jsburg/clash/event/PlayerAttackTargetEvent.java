package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.weapons.util.IClashWeapon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Clash.MOD_ID)
public class PlayerAttackTargetEvent {

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack heldItemStack = player.inventory.getCurrentItem();
        Item heldItem = heldItemStack.getItem();
        if (heldItem instanceof IClashWeapon) {
            ((IClashWeapon)heldItem).onHit(heldItemStack, player, event.getTarget());
        }
    }
}
