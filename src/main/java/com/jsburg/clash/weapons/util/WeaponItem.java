package com.jsburg.clash.weapons.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.List;

@SuppressWarnings("deprecation")
public class WeaponItem extends Item implements IVanishable {
    private final Multimap<Attribute, AttributeModifier> attributes;

    public WeaponItem(int attackDamage, float attackSpeed, Item.Properties properties) {
        super(properties.group(ItemGroup.COMBAT));
        // Corrects numbers so that the constructor can simply use the value displayed on the tooltip
        attackDamage -= 1;
        attackSpeed = -(4 - attackSpeed);
        float attackDamage1 = (float) attackDamage;

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage1, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));

        this.attributes = builder.build();
    }

    protected Multimap<Attribute, AttributeModifier> getAttributes() {
        return this.attributes;
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.attributes : super.getAttributeModifiers(equipmentSlot);
    }

    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(1, attacker, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        List<Enchantment> enchants = this.vanillaEnchantments();
        if (enchants != null && enchants.contains(enchantment)) {
            return true;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    protected List<Enchantment> vanillaEnchantments() {
        return null;
    }


    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity target) {

        if (AttackHelper.canAttackEntity(player, target)) {
            this.onHit(stack, player, target);
        }

        return super.onLeftClickEntity(stack, player, target);
    }

    protected void onHit(ItemStack stack, PlayerEntity player, Entity target) {}

}
