package com.jsburg.clash.enchantments.spear;

import com.jsburg.clash.registry.AllEnchantments;
import com.jsburg.clash.registry.AllParticles;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class DashEnchantment extends Enchantment {

    public DashEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 12;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return 50;
    }

    @Override
    protected boolean canApplyTogether(Enchantment ench) {
        if (ench instanceof ThrustEnchantment) return false;
        return super.canApplyTogether(ench);
    }

    public static void tryAgilityDash(World worldIn, PlayerEntity playerIn, ItemStack stack) {
        if (EnchantmentHelper.getEnchantmentLevel(AllEnchantments.AGILITY.get(), stack) > 0) {
            if (!(playerIn.isSneaking() || playerIn.isElytraFlying() || playerIn.isSwimming())) {
                double ySpeed = playerIn.isOnGround() ? .25 : -.4;
                final float dashSpeed = playerIn.isOnGround() ? .7f : .6f;

                Vector3d motion = playerIn.getMotion();
                Vector3d dir = new Vector3d(motion.getX(), 0, motion.getZ());

                if (Math.abs(dir.x) <= 0.05 && Math.abs(dir.z) <= 0.05) {
                    Vector3d look = playerIn.getLookVec();
                    dir = new Vector3d(-look.getX(), 0, -look.getZ());
                }
                dir = dir.normalize().scale(dashSpeed);

                playerIn.addVelocity(dir.x, ySpeed, dir.z);
                Vector3d dashDir = new Vector3d(dir.x, ySpeed/2, dir.z);
                Random rand = worldIn.getRandom();
                int o = 7 + rand.nextInt(4);
                for (int i = 0; i <= o; i++){
                    Vector3d d = dashDir.rotateYaw((rand.nextFloat() * .5f) - .25f).scale(rand.nextFloat() * .5 + .25);
                    worldIn.addParticle(AllParticles.DASH_DUST.get(),
                            playerIn.getPosX() + d.getX()/2, playerIn.getPosY() + d.getY() + .1, playerIn.getPosZ() + d.getZ()/2,
                            d.getX(), d.getY() + rand.nextFloat() * .1 - .05, d.getZ());
                }

                playerIn.addExhaustion(0.1f);
                playerIn.getCooldownTracker().setCooldown(stack.getItem(), 15);
            }
        }
    }
}
