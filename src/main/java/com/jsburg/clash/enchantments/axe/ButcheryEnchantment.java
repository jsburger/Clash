package com.jsburg.clash.enchantments.axe;

import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.registry.MiscRegistry;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class ButcheryEnchantment extends Enchantment {

    public ButcheryEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 6;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 10;
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
        return target.getType().isContained(MiscRegistry.PORKY);
    }

    @Override
    protected boolean canApplyTogether(Enchantment enchantment) {
        if (enchantment instanceof DamageEnchantment) return false;
        return super.canApplyTogether(enchantment);
    }

    public static void onHit(int level, LivingEntity target) {
        if (!target.world.isRemote) {
            Random rand = target.world.getRandom();
            AxisAlignedBB bb = target.getBoundingBox();
            double l = bb.getAverageEdgeLength();
            Vector3d pos = new Vector3d((rand.nextDouble() - .5) * l, rand.nextDouble() * l + .5, (rand.nextDouble() - .5) * l);
            AttackHelper.makeParticleServer((ServerWorld) target.world, AllParticles.BUTCHER_SPARK_EMITTER.get(), target.getPositionVec().add(pos), Vector3d.ZERO, 0);
        }
    }

}
