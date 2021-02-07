package com.jsburg.clash.weapons.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

@SuppressWarnings("deprecation")
public class WeaponItem extends Item implements IVanishable, IClashWeapon {
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> attributes;

    public WeaponItem(int attackDamage, float attackSpeed, Item.Properties properties) {
        super(properties.group(ItemGroup.COMBAT));
        // Corrects numbers so that the constructor can simply use the value displayed on the tooltip
        attackDamage -= 1;
        attackSpeed *= -1;
        this.attackDamage = (float)attackDamage;

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));

        this.attributes = builder.build();
    }

    public float getAttackDamage() {
        return attackDamage;
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

}
