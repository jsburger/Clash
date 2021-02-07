package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.weapons.SpearItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Clash.MOD_ID);

    public static final RegistryObject<Item> SPEAR = ITEMS.register("spear", () -> new SpearItem(4, -2.0F, (new Item.Properties()).group(ItemGroup.COMBAT).maxDamage(852)));

}
