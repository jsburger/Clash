package com.jsburg.clash.weapons.util;

import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;

public class AttackHelper {
    public static boolean canAttackEntity(PlayerEntity player, Entity targetEntity) {
        return (targetEntity.canBeAttackedWithItem() && !targetEntity.hitByEntity(player));
    }

    /**
     * Runs Forge's player attack event and the entity's hit check
     */
    public static boolean fullAttackEntityCheck(PlayerEntity player, Entity targetEntity) {
        if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(player, targetEntity)) return false;
        return canAttackEntity(player, targetEntity);
    }
    public static boolean weaponIsCharged(PlayerEntity player) {
        return player.getCooledAttackStrength(0.5F) > 0.9F;
    }
    public static void playSound(PlayerEntity player, SoundEvent sound) {
        playSound(player, sound, 1.0F, 1.0F);
    }
    public static void playSound(PlayerEntity player, SoundEvent sound, float volume, float pitch) {
        player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), sound, player.getSoundCategory(), volume, pitch);
    }

    /**
     * Calls the item's hit method, which needs a target. Will reduce weapon durability.
     */
    public static void doHitStuff(PlayerEntity player, Entity target, ItemStack weapon) {
        doHitStuff(player, target, weapon, player.getActiveHand());
    }
    //Referenced from net.minecraft.entity.player.PlayerEntity.attackTargetEntityWithCurrentItem
    public static void doHitStuff(PlayerEntity player, Entity target, ItemStack weapon, Hand hand) {
        if (target instanceof EnderDragonPartEntity) {
            target = ((EnderDragonPartEntity)target).dragon;
        }
        if (!player.world.isRemote && target instanceof LivingEntity) {
            ItemStack copy = weapon.copy();
            weapon.hitEntity((LivingEntity) target, player);
            if (weapon.isEmpty()) {
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, hand);
                player.setHeldItem(hand, ItemStack.EMPTY);
            }
        }
    }

    public static void damageItem(int damage, ItemStack item, PlayerEntity player) {
        damageItem(damage, item, player, Hand.MAIN_HAND);
    }
    public static void damageItem(int damage, ItemStack item, PlayerEntity player, Hand hand) {
        item.damageItem(damage, player, (p) -> {
            p.sendBreakAnimation(hand);
            net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, item, hand);
        });
    }

    /**
     * Hits entity, spawns particles, adds stats.
     */
    public static void attackEntity(PlayerEntity player, Entity target, float damage) {

        float lastHealth = 0;
        if (target instanceof LivingEntity) {
            lastHealth = ((LivingEntity) target).getHealth();
        }
        target.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);

        if (target instanceof LivingEntity) {
            float healthDifference = lastHealth - ((LivingEntity) target).getHealth();
            player.addStat(Stats.DAMAGE_DEALT, Math.round(healthDifference * 10));

            if (player.world instanceof ServerWorld && healthDifference > 2.0F) {
                int k = (int)(healthDifference * 0.5D);
                ((ServerWorld)player.world)
                        .spawnParticle(
                                ParticleTypes.DAMAGE_INDICATOR,
                                target.getPosX(),
                                target.getPosYHeight(0.5),
                                target.getPosZ(),
                                k, 0.1, 0.0, 0.1, 0.2);
            }
        }

    }

    /**
     * Gets bonus from things like sharpness, smite, etc.
     * Returns the additional damage, which is flat, apparently.
     * Multiply it by the attack's charge percent. Add it after multiplying by crits.
     */
    public static float getBonusEnchantmentDamage(ItemStack item, Entity target) {
        float bonus;
        if (target instanceof LivingEntity) {
            bonus = EnchantmentHelper.getModifierForCreature(item, ((LivingEntity)target).getCreatureAttribute());
        } else {
            bonus = EnchantmentHelper.getModifierForCreature(item, CreatureAttribute.UNDEFINED);
        }
        return bonus;
    }

    public static float getCrit(PlayerEntity player, Entity target, boolean shouldCrit) {
        return getCrit(player, target, shouldCrit, true, true);
    }

    public static float getCrit(PlayerEntity player, Entity target, boolean shouldCrit, boolean doEffects, boolean playCritSound) {
        net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, target, shouldCrit, shouldCrit ? 1.5F : 1.0F);
        if (hitResult == null) {
            return 1;
        }
        if (hitResult.getDamageModifier() > 1 && doEffects) {
            if (playCritSound) playSound(player, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT);
            player.onCriticalHit(target);
        }
        return hitResult.getDamageModifier();
    }

    //I feel like this shouldn't be necessary. At least I don't have to write the packets myself, I guess.
    public static void makeParticle(World world, BasicParticleType particle, Vector3d position, Vector3d motion, double speed) {
        motion = motion.normalize().scale(speed);
        world.addParticle(particle, position.getX(), position.getY(), position.getZ(), motion.getX(), motion.getY(), motion.getZ());
    }

    public static double getAttackDamage(ItemStack item, PlayerEntity player, EquipmentSlotType equipmentSlot) {
        Multimap<Attribute, AttributeModifier> multimap = item.getAttributeModifiers(equipmentSlot);

        for(Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
            if (entry.getKey().equals(Attributes.ATTACK_DAMAGE)) {
                return entry.getValue().getAmount() + player.getBaseAttributeValue(Attributes.ATTACK_DAMAGE);
            }
        }

        return player.getBaseAttributeValue(Attributes.ATTACK_DAMAGE);
    }

}
