package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreativeModeTabEvents {

    private static final List<Supplier<? extends ItemLike>> items = new ArrayList<>();
    private static final List<ResourceKey<CreativeModeTab>> tabs = new ArrayList<>();

    @SafeVarargs
    public static void assignItemToTab(Supplier<? extends ItemLike> item, ResourceKey<CreativeModeTab>... in_tabs) {
        for (ResourceKey<CreativeModeTab> tab : in_tabs) {
            items.add(item);
            tabs.add(tab);
        }
    }

    @SubscribeEvent
    public static void onCreativeTabRegister(BuildCreativeModeTabContentsEvent event) {
        int i = 0;
        for (Supplier<? extends ItemLike> item : items) {
            if (event.getTabKey() == tabs.get(i)) {
                event.accept(item);
            }
            i++;
        }
//        tabs.clear();
//        items.clear();
    }


}