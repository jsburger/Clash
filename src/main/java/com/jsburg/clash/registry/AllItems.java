package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.weapons.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class AllItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Clash.MOD_ID);

    public static final RegistryObject<Item> SPEAR = ITEMS.register("spear", () -> new SpearItem(5, 2.0F, (new Item.Properties()).durability(852)));
    public static final RegistryObject<Item> BILLHOOK = ITEMS.register("billhook", () -> new BillhookItem(5, 2.0F, (new Item.Properties()).durability(852)));
    public static final RegistryObject<Item> SWEPT_AXE = ITEMS.register("swept_axe", () -> new SweptAxeItem(6, 0.9F, (new Item.Properties()).durability(650)));
    public static final RegistryObject<Item> SWEPT_AXE_HEAD = ITEMS.register("swept_axe_head", () -> new Item(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> GREATBLADE = ITEMS.register("greatblade", () -> new GreatbladeItem(5, 0.8F,
            (new Item.Properties()).durability(896)));
    public static final RegistryObject<Item> ROD_OF_GALES = ITEMS.register("rod_of_gales", () -> new JumpRodItem(2, 2.0F, (new Item.Properties()).durability(612)));
    //Rescue rod

    public static void registerItemProperties() {
        ItemProperties.register(GREATBLADE.get(), new ResourceLocation(Clash.MOD_ID, "has_thrum"),
                (itemStack, world, entity, magicNumber) -> GreatbladeItem.hasThrum(itemStack) ? 1 : 0);
    }

}
