package com.jsburg.clash.enchantments.axe;

import com.jsburg.clash.enchantments.ClashEnchantment;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.registry.MiscRegistry;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ButcheryEnchantment extends ClashEnchantment {

    public ButcheryEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 6;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 10;
    }

    //Called in LivingEvents
    public static float getDamageMultiplier(int level) {
        return 1 + (.15f * level);
    }

    public static int getPorkAmount(int level, Random random) {
        //50/50 chance to add porkchop for every level
        int count = 0;
        for (int n = 1; n <= level; n++) {
            if (random.nextFloat() <= .5) {
                count++;
            }
        }
        return count;
    }

    public static boolean affectsEntity(LivingEntity target) {
        return target.getType().is(MiscRegistry.PORKY);
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        if (enchantment instanceof DamageEnchantment) return false;
        return super.checkCompatibility(enchantment);
    }

    public static void onHit(int level, LivingEntity target) {
        if (!target.level.isClientSide) {
            Random rand = target.level.getRandom();
            AABB bb = target.getBoundingBox();
            double l = bb.getSize();
            Vec3 pos = new Vec3((rand.nextDouble() - .5) * l, rand.nextDouble() * l + .5, (rand.nextDouble() - .5) * l);
            AttackHelper.makeParticleServer((ServerLevel) target.level, AllParticles.BUTCHER_SPARK_EMITTER.get(), target.position().add(pos), Vec3.ZERO, 0);
        }
    }

}
