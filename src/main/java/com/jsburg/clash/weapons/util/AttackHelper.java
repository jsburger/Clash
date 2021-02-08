package com.jsburg.clash.weapons.util;

import com.jsburg.clash.registry.AllSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;

public class AttackHelper {
    public static boolean canAttackEntity(PlayerEntity player, Entity targetEntity) {
        return (targetEntity.canBeAttackedWithItem() && !targetEntity.hitByEntity(player));
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
}
