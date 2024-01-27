package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.registry.AllItems;
import com.jsburg.clash.weapons.SweptAxeItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemBreakEvent {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onItemBroken(PlayerDestroyItemEvent event) {
        ItemStack item = event.getOriginal();
        //Drop Swept axe head
        if (item.getItem() instanceof SweptAxeItem) {
            ItemStack headItem = new ItemStack(AllItems.SWEPT_AXE_HEAD.get(), 1);
            EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(item), headItem);
            Player player = event.getEntity();
            player.getCommandSenderWorld().addFreshEntity(new ItemEntity(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), headItem));
        }
    }
}
