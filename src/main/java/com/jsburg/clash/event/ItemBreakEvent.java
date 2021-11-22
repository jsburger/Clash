package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.registry.AllItems;
import com.jsburg.clash.weapons.SweptAxeItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemBreakEvent {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onItemBroken(PlayerDestroyItemEvent event) {
        ItemStack item = event.getOriginal();
        if (item.getItem() instanceof SweptAxeItem) {
            ItemStack headItem = new ItemStack(AllItems.SWEPT_AXE_HEAD.get(), 1);
            EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(item), headItem);
            PlayerEntity player = event.getPlayer();
            player.getEntityWorld().addEntity(new ItemEntity(player.getEntityWorld(), player.getPosX(), player.getPosY(), player.getPosZ(), headItem));
        }
    }
}
