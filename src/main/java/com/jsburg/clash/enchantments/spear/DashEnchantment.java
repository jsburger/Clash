package com.jsburg.clash.enchantments.spear;

import com.jsburg.clash.enchantments.ClashEnchantment;
import com.jsburg.clash.registry.AllEnchantments;
import com.jsburg.clash.registry.AllParticles;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DashEnchantment extends ClashEnchantment {

    public DashEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    public int getMinCost(int enchantmentLevel) {
        return 12;
    }

    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        if (ench instanceof ThrustEnchantment) return false;
        return super.checkCompatibility(ench);
    }

    public static void tryAgilityDash(Level worldIn, Player playerIn, ItemStack stack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.AGILITY.get(), stack) > 0) {
            if (!(playerIn.isShiftKeyDown() || playerIn.isFallFlying() || playerIn.isSwimming())) {
                double ySpeed = playerIn.onGround() ? .25 : -.4;
                final float dashSpeed = playerIn.onGround() ? .7f : .6f;

                Vec3 motion = playerIn.getDeltaMovement();
                Vec3 dir = new Vec3(motion.x(), 0, motion.z());

                if (Math.abs(dir.x) <= 0.05 && Math.abs(dir.z) <= 0.05) {
                    Vec3 look = playerIn.getLookAngle();
                    dir = new Vec3(-look.x(), 0, -look.z());
                }
                dir = dir.normalize().scale(dashSpeed);

                playerIn.push(dir.x, ySpeed, dir.z);
                Vec3 dashDir = new Vec3(dir.x, ySpeed/2, dir.z);
                RandomSource rand = worldIn.getRandom();
                int o = 7 + rand.nextInt(4);
                for (int i = 0; i <= o; i++){
                    Vec3 d = dashDir.yRot((rand.nextFloat() * .5f) - .25f).scale(rand.nextFloat() * .5 + .25);
                    worldIn.addParticle(AllParticles.DASH_DUST.get(),
                            playerIn.getX() + d.x()/2, playerIn.getY() + d.y() + .1, playerIn.getZ() + d.z()/2,
                            d.x(), d.y() + rand.nextFloat() * .1 - .05, d.z());
                }

                playerIn.causeFoodExhaustion(0.1f);
                playerIn.getCooldowns().addCooldown(stack.getItem(), 15);
            }
        }
    }
}
