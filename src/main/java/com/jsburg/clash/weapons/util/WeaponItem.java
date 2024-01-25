package com.jsburg.clash.weapons.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@SuppressWarnings("deprecation")
public class WeaponItem extends Item implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> attributes;

    public WeaponItem(float attackDamage, float attackSpeed, Item.Properties properties) {
        super(properties.tab(CreativeModeTab.TAB_COMBAT));
        // Corrects numbers so that the constructor can simply use the value displayed on the tooltip
        attackDamage -= 1;
        attackSpeed = -(4 - attackSpeed);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));

        this.attributes = builder.build();
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    protected Multimap<Attribute, AttributeModifier> getAttributes() {
        return this.attributes;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.attributes : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
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

// Unused for now
//    @Override
//    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity target) {
//
//        if (AttackHelper.canAttackEntity(player, target)) {
//            this.onHit(stack, player, target);
//        }
//
//        return super.onLeftClickEntity(stack, player, target);
//    }
//
//    protected void onHit(ItemStack stack, PlayerEntity player, Entity target) {}

}
