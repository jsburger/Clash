package com.jsburg.clash.weapons;

import com.jsburg.clash.registry.AllEffects;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.util.ScreenShaker;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Arrays;
import java.util.List;

public class SweptAxeItem extends WeaponItem {

    public SweptAxeItem(int attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    @Override
    public List<Enchantment> vanillaEnchantments() {
        return Arrays.asList(Enchantments.LOOTING);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment instanceof DamageEnchantment) return true;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity target) {
        if (AttackHelper.weaponIsCharged(player)) {

            //Sweep Particle
            EffectInstance retaliation = player.getActivePotionEffect(AllEffects.RETALIATION.get());
            Vector3d eyepos = player.getPositionVec().add(0, player.getEyeHeight(), 0);
            AttackHelper.makeParticle(target.world, AllParticles.AXE_SWEEP.get(),
                    target.getPositionVec().add(eyepos).scale(.5),
                    //Axe sweep particle uses xSpeed as scale, ySpeed as being red, zSpeed is horizontal flip
                    .5, (retaliation != null) ? 1 : 0, player.getPrimaryHand() == HandSide.LEFT ? 1 : 0
            );

//            if (player.world.isRemote) {
//                Random rand = player.world.getRandom();
//                AxisAlignedBB bb = target.getBoundingBox();
//                double l = bb.getAverageEdgeLength();
//                Vector3d pos = new Vector3d((rand.nextDouble() - .5) * l, rand.nextDouble() * l + .5, (rand.nextDouble() - .5) * l);
//                AttackHelper.makeParticle(player.world, AllParticles.BUTCHER_SPARK_EMITTER.get(), target.getPositionVec().add(pos));
//            }

            //Used for knockback
            Vector3d look = player.getLookVec();
            //Calculate damage dealt
            float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            int hits = 1;

            //Get entities to sweep, almost directly copied from sword sweeping code.
            for(LivingEntity livingentity : player.world.getEntitiesWithinAABB(LivingEntity.class, target.getBoundingBox().grow(1.5D, 0.5D, 1.5D))) {
                if (livingentity != player && livingentity != target && !player.isOnSameTeam(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).hasMarker()) && player.getDistanceSq(livingentity) < 12.0D) {
                    //Skip over pets tamed by the player
                    if (livingentity instanceof TameableEntity) {
                        if (((TameableEntity) livingentity).isOwner(player)) {
                            continue;
                        }
                    }
                    if (!player.world.isRemote) {
                        livingentity.applyKnockback(0.4f, -look.getX(), -look.getZ());
                        float bonus = AttackHelper.getBonusEnchantmentDamage(stack, livingentity);
                        if (livingentity.attackEntityFrom(DamageSource.causePlayerDamage(player), damage + bonus)) {
                            if (bonus > 0) {
                                player.onEnchantmentCritical(livingentity);
                            }
                        }
                    }
                    hits++;
                }
            }

            if (player.world.isRemote && hits >= 3) {
                ScreenShaker.setScreenShake(4, 2);
            }


            //Sweep Sound
            AttackHelper.playSound(player, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, .7f);
        }
        return false;
    }

}
