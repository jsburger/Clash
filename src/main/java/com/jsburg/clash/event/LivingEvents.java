package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.enchantments.axe.ButcheryEnchantment;
import com.jsburg.clash.enchantments.axe.RampageEnchantment;
import com.jsburg.clash.enchantments.axe.RetaliationEnchantment;
import com.jsburg.clash.registry.AllEffects;
import com.jsburg.clash.registry.AllEnchantments;
import com.jsburg.clash.registry.AllItems;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;


@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingEvents {

    //Referenced from Occultism's Butcher's Knife
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.isRecentlyHit() && wasMeleeCaused(event.getSource())) {
            LivingEntity source = (LivingEntity) event.getSource().getEntity();
            LivingEntity target = event.getEntityLiving();

            //Check for Butchery drops
            ItemStack weapon = source.getMainHandItem();
            int butcherLevel = EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.BUTCHERY.get(), weapon);
            if (butcherLevel > 0 && ButcheryEnchantment.affectsEntity(target) && (weapon.getItem() != AllItems.SWEPT_AXE_HEAD.get())) {
                //Spawn bonus Pork Chops
                Random random = target.getCommandSenderWorld().getRandom();
                int porkCount = ButcheryEnchantment.getPorkAmount(butcherLevel, random);
                if (porkCount > 0) {
                    ItemEntity porkDrop = new ItemEntity(target.getCommandSenderWorld(),
                            target.getX() + random.nextDouble() - .5,
                            target.getY() + random.nextDouble() + .5,
                            target.getZ() + random.nextDouble() - .5,
                            new ItemStack(Items.PORKCHOP, porkCount));
                    if (!target.getCommandSenderWorld().isClientSide) {
                        Vec3 pos = AttackHelper.getEntityPosition(porkDrop);
                        AttackHelper.makeParticleServer((ServerLevel) target.getCommandSenderWorld(), AllParticles.BONUS_DROP.get(), pos, Vec3.ZERO, 0);
                    }
                    event.getDrops().add(porkDrop);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        //CHECK FOR MELEE ATTACKS
        if (wasMeleeCaused(event.getSource())) {
            LivingEntity source = (LivingEntity) event.getSource().getEntity();
            LivingEntity target = event.getEntityLiving();

            ItemStack weapon = source.getMainHandItem();
            //Butchery Effect
            int butcherLevel = EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.BUTCHERY.get(), weapon);
            if (butcherLevel > 0 && ButcheryEnchantment.affectsEntity(target) && (weapon.getItem() != AllItems.SWEPT_AXE_HEAD.get())) {
                event.setAmount(event.getAmount() * ButcheryEnchantment.getDamageMultiplier(butcherLevel));
                ButcheryEnchantment.onHit(butcherLevel, target);
            }
        }
        //PLAYER GETTING HURT
        if (event.getEntityLiving() instanceof Player) {
            Player hurtPlayer = (Player) event.getEntityLiving();
            ItemStack heldItem = hurtPlayer.getMainHandItem();

            //Retaliation effect
            if (EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.RETALIATION.get(), heldItem) > 0) {
                RetaliationEnchantment.onUserHurt(hurtPlayer, EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.RETALIATION.get(), heldItem));
            }
        }
        //Remove stagger upon being hurt
        if (event.getEntityLiving().getEffect(AllEffects.STAGGERED.get()) != null) {
            event.getEntityLiving().removeEffect(AllEffects.STAGGERED.get());
        }
    }

    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        if (wasMeleeCaused(event.getSource())) {
            LivingEntity source = (LivingEntity) event.getSource().getEntity();
            LivingEntity target = event.getEntityLiving();

            ItemStack weapon = source.getMainHandItem();
            int rampageLevel = EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.RAMPAGE.get(), weapon);
            if (rampageLevel > 0) {
                RampageEnchantment.onKill(source, rampageLevel);
            }
        }
    }

    private static boolean wasMeleeCaused(DamageSource source) {
        return (source.getEntity() instanceof LivingEntity && isMeleeDamage(source));
    }

    private static boolean isMeleeDamage(DamageSource source) {
        return source.getDirectEntity() == source.getEntity();
    }
}
