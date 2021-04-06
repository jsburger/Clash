package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.enchantments.FlurryEnchantment;
import com.jsburg.clash.enchantments.ThrustEnchantment;
import com.jsburg.clash.enchantments.TipperEnchantment;
import com.jsburg.clash.weapons.SpearItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Clash.MOD_ID);

    public static final EnchantmentType SPEAR = EnchantmentType.create("spear", item -> (item instanceof SpearItem));

    public static final RegistryObject<Enchantment> FLURRY = ENCHANTMENTS.register("flurry", () -> new FlurryEnchantment(Enchantment.Rarity.UNCOMMON, SPEAR, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> LUNGE = ENCHANTMENTS.register("lunge", () -> new ThrustEnchantment(Enchantment.Rarity.UNCOMMON, SPEAR, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> SWEET_SPOT = ENCHANTMENTS.register("sweet_spot", () -> new TipperEnchantment(Enchantment.Rarity.RARE, SPEAR, EquipmentSlotType.MAINHAND));

}
