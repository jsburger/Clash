package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.event.CreativeModeTabEvents;
import com.jsburg.clash.weapons.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class AllItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Clash.MOD_ID);

    public static final RegistryObject<Item> SPEAR = register("spear", () -> new SpearItem(5, 2.0F, (new Item.Properties()).durability(852)));
    public static final RegistryObject<Item> BILLHOOK = register("billhook", () -> new BillhookItem(5, 2.0F, (new Item.Properties()).durability(852)));
    public static final RegistryObject<Item> SWEPT_AXE = register("swept_axe", () -> new SweptAxeItem(6, 0.9F, (new Item.Properties()).durability(650)));
    public static final RegistryObject<Item> SWEPT_AXE_HEAD = register("swept_axe_head", () -> new Item(new Item.Properties().stacksTo(1)), CreativeModeTabs.INGREDIENTS);
    public static final RegistryObject<Item> GREATBLADE = register("greatblade", () -> new GreatbladeItem(5, 0.8F,
            (new Item.Properties()).durability(896)));
    public static final RegistryObject<Item> ROD_OF_GALES = register("rod_of_gales", () -> new JumpRodItem(2, 2.0F, (new Item.Properties()).durability(612)));
    //Rescue rod

    public static void registerItemProperties() {
        ItemProperties.register(GREATBLADE.get(), new ResourceLocation(Clash.MOD_ID, "has_thrum"),
                (itemStack, world, entity, magicNumber) -> GreatbladeItem.hasThrum(itemStack) ? 1 : 0);
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return register(name, supplier, CreativeModeTabs.COMBAT);
    }
    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> supplier, ResourceKey<CreativeModeTab> tab) {
        var i = ITEMS.register(name, supplier);
        CreativeModeTabEvents.assignItemToTab(i, tab);
        return i;
    }

}
