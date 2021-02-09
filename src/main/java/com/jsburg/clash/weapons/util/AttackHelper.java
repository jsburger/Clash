package com.jsburg.clash.weapons.util;

import com.jsburg.clash.registry.AllSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.server.ServerWorld;

public class AttackHelper {
    public static boolean canAttackEntity(PlayerEntity player, Entity targetEntity) {
        return (targetEntity.canBeAttackedWithItem() && !targetEntity.hitByEntity(player));
    }
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

    public static void damageTool(PlayerEntity player, Entity target, ItemStack weapon) {
        damageTool(player, target, weapon, player.getActiveHand());
    }
    //Referenced from net.minecraft.entity.player.PlayerEntity.attackTargetEntityWithCurrentItem
    public static void damageTool(PlayerEntity player, Entity target, ItemStack weapon, Hand hand) {
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

    //Hits entity, spawns particles, adds stats.
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
}
