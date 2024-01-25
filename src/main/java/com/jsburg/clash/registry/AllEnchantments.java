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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Clash.MOD_ID);

    public static final EnchantmentCategory SPEAR = EnchantmentCategory.create("spear", item -> (item instanceof SpearItem));
    public static final EnchantmentCategory SWEPT = EnchantmentCategory.create("swept", item -> (item instanceof SweptAxeItem));
    public static final EnchantmentCategory GREATBLADE = EnchantmentCategory.create("greatblade", item -> (item instanceof GreatbladeItem));
    public static final EnchantmentCategory AGILITY_ITEMS = EnchantmentCategory.create("agility", item -> (item instanceof SpearItem || item instanceof JumpRodItem));

    public static final RegistryObject<Enchantment> FLURRY = ENCHANTMENTS.register("flurry", () -> new FlurryEnchantment(Rarity.UNCOMMON, SPEAR, EquipmentSlot.MAINHAND));
//    public static final RegistryObject<Enchantment> LUNGE = ENCHANTMENTS.register("lunge", () -> new ThrustEnchantment(Enchantment.Rarity.UNCOMMON, SPEAR, EquipmentSlotType.MAINHAND));
    public static final RegistryObject<Enchantment> SWEET_SPOT = ENCHANTMENTS.register("sweet_spot", () -> new TipperEnchantment(Rarity.RARE, SPEAR, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> AGILITY = ENCHANTMENTS.register("agility", () -> new DashEnchantment(Rarity.RARE, AGILITY_ITEMS, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> JAB = ENCHANTMENTS.register("jab", () -> new JabEnchantment(Rarity.VERY_RARE, SPEAR, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> BUTCHERY = ENCHANTMENTS.register("butchery", () -> new ButcheryEnchantment(Rarity.UNCOMMON, SWEPT, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> RAMPAGE = ENCHANTMENTS.register("rampage", () -> new RampageEnchantment(Rarity.RARE, SWEPT, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> RETALIATION = ENCHANTMENTS.register("retaliation", () -> new RetaliationEnchantment(Rarity.COMMON, SWEPT, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> SAILING = ENCHANTMENTS.register("sailing", () -> new MarkerEnchantment(10, 30, Rarity.UNCOMMON, GREATBLADE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> CRUSHING = ENCHANTMENTS.register("crushing", () -> new CrushingEnchantment(Rarity.COMMON, GREATBLADE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> EXECUTIONER = ENCHANTMENTS.register("executioner", () -> new MarkerEnchantment(17, 50, Rarity.VERY_RARE, GREATBLADE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> THRUM = ENCHANTMENTS.register("thrum", () -> new ThrumEnchantment(Rarity.COMMON, GREATBLADE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> WHIRLING = ENCHANTMENTS.register("whirling", () -> new WhirlingEnchant(Rarity.COMMON, GREATBLADE, EquipmentSlot.MAINHAND));
}
