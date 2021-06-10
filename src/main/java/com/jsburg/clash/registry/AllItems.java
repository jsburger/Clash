package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.weapons.SpearItem;
import com.jsburg.clash.weapons.SweptAxeItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public class AllItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Clash.MOD_ID);

    public static final RegistryObject<Item> SPEAR = ITEMS.register("spear", () -> new SpearItem(5, 2.0F, (new Item.Properties()).maxDamage(852)));
    public static final RegistryObject<Item> SWEPT_AXE = ITEMS.register("swept_axe", () -> new SweptAxeItem(6, 0.7F, (new Item.Properties()).maxDamage(1220)));

}
