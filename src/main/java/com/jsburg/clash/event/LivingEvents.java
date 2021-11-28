package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.enchantments.axe.ButcheryEnchantment;
import com.jsburg.clash.registry.AllEnchantments;
import com.jsburg.clash.registry.AllItems;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingEvents {

    //Referenced from Occultism's Butcher's Knife
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.isRecentlyHit() && event.getSource().getTrueSource() instanceof LivingEntity && event.getSource().getImmediateSource() == event.getSource().getTrueSource()) {
            LivingEntity source = (LivingEntity) event.getSource().getTrueSource();
            LivingEntity target = event.getEntityLiving();

            ItemStack weapon = source.getHeldItemMainhand();
            int butcherLevel = EnchantmentHelper.getEnchantmentLevel(AllEnchantments.BUTCHERY.get(), weapon);
            if (butcherLevel > 0 && ButcheryEnchantment.affectsEntity(target) && (weapon.getItem() != AllItems.SWEPT_AXE_HEAD.get())) {
                Random random = target.getEntityWorld().getRandom();
                int porkCount = ButcheryEnchantment.getPorkAmount(butcherLevel, random);
                if (porkCount > 0) {
                    ItemEntity porkDrop = new ItemEntity(target.getEntityWorld(),
                            target.getPosX() + random.nextDouble() - .5,
                            target.getPosY() + random.nextDouble() + .5,
                            target.getPosZ() + random.nextDouble() - .5,
                            new ItemStack(Items.PORKCHOP, porkCount));
                    if (!target.getEntityWorld().isRemote) {
                        Vector3d pos = AttackHelper.getEntityPosition(porkDrop);
                        AttackHelper.makeParticleServer((ServerWorld) target.getEntityWorld(), AllParticles.BONUS_DROP.get(), pos, Vector3d.ZERO, 0);
                    }
                    event.getDrops().add(porkDrop);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if (event.getSource().getTrueSource() instanceof LivingEntity && event.getSource().getImmediateSource() == event.getSource().getTrueSource()) {
            LivingEntity source = (LivingEntity) event.getSource().getTrueSource();
            LivingEntity target = event.getEntityLiving();

            ItemStack weapon = source.getHeldItemMainhand();
            int butcherLevel = EnchantmentHelper.getEnchantmentLevel(AllEnchantments.BUTCHERY.get(), weapon);
            if (butcherLevel > 0 && ButcheryEnchantment.affectsEntity(target) && (weapon.getItem() != AllItems.SWEPT_AXE_HEAD.get())) {
                event.setAmount(event.getAmount() * ButcheryEnchantment.getDamageMultiplier(butcherLevel));
                ButcheryEnchantment.onHit(butcherLevel, target);
            }
        }
    }
}
