package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.enchantments.MarkerEnchantment;
import com.jsburg.clash.enchantments.axe.ButcheryEnchantment;
import com.jsburg.clash.enchantments.axe.RampageEnchantment;
import com.jsburg.clash.enchantments.axe.RetaliationEnchantment;
import com.jsburg.clash.enchantments.greatblade.CrushingEnchantment;
import com.jsburg.clash.enchantments.greatblade.ThrumEnchantment;
import com.jsburg.clash.enchantments.greatblade.WhirlingEnchant;
import com.jsburg.clash.enchantments.spear.DashEnchantment;
import com.jsburg.clash.enchantments.spear.FlurryEnchantment;
import com.jsburg.clash.enchantments.spear.JabEnchantment;
import com.jsburg.clash.enchantments.spear.TipperEnchantment;
import com.jsburg.clash.weapons.GreatbladeItem;
import com.jsburg.clash.weapons.JumpRodItem;
import com.jsburg.clash.weapons.SpearItem;
import com.jsburg.clash.weapons.SweptAxeItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Clash.MOD_ID);

    public static final EnchantmentType SPEAR = EnchantmentType.create("spear", item -> (item instanceof SpearItem));
    public static final EnchantmentType SWEPT = EnchantmentType.create("swept", item -> (item instanceof SweptAxeItem));
    public static final EnchantmentType GREATBLADE = EnchantmentType.create("greatblade", item -> (item instanceof GreatbladeItem));
    public static final EnchantmentType AGILITY_ITEMS = EnchantmentType.create("agility", item -> (item instanceof SpearItem || item instanceof JumpRodItem));

    public static final RegistryObject<Enchantment> FLURRY = ENCHANTMENTS.register("flurry", () -> new FlurryEnchantment(Rarity.UNCOMMON, SPEAR, EquipmentSlotType.MAINHAND));
//    public static final RegistryObject<Enchantment> LUNGE = ENCHANTMENTS.register("lunge", () -> new ThrustEnchantment(Enchantment.Rarity.UNCOMMON, SPEAR, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> SWEET_SPOT = ENCHANTMENTS.register("sweet_spot", () -> new TipperEnchantment(Rarity.RARE, SPEAR, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> AGILITY = ENCHANTMENTS.register("agility", () -> new DashEnchantment(Rarity.RARE, AGILITY_ITEMS, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> JAB = ENCHANTMENTS.register("jab", () -> new JabEnchantment(Rarity.VERY_RARE, SPEAR, EquipmentSlotType.MAINHAND));

    public static final RegistryObject<Enchantment> BUTCHERY = ENCHANTMENTS.register("butchery", () -> new ButcheryEnchantment(Rarity.UNCOMMON, SWEPT, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> RAMPAGE = ENCHANTMENTS.register("rampage", () -> new RampageEnchantment(Rarity.RARE, SWEPT, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> RETALIATION = ENCHANTMENTS.register("retaliation", () -> new RetaliationEnchantment(Rarity.COMMON, SWEPT, EquipmentSlotType.MAINHAND));

    public static final RegistryObject<Enchantment> SAILING = ENCHANTMENTS.register("sailing", () -> new MarkerEnchantment(10, 30, Rarity.UNCOMMON, GREATBLADE, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> CRUSHING = ENCHANTMENTS.register("crushing", () -> new CrushingEnchantment(Rarity.COMMON, GREATBLADE, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> EXECUTIONER = ENCHANTMENTS.register("executioner", () -> new MarkerEnchantment(17, 50, Rarity.VERY_RARE, GREATBLADE, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> THRUM = ENCHANTMENTS.register("thrum", () -> new ThrumEnchantment(Rarity.COMMON, GREATBLADE, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> WHIRLING = ENCHANTMENTS.register("whirling", () -> new WhirlingEnchant(Rarity.COMMON, GREATBLADE, EquipmentSlotType.MAINHAND));
}
