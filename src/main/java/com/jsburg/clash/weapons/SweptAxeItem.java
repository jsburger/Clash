package com.jsburg.clash.weapons;

import com.jsburg.clash.registry.AllEffects;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.util.ScreenShaker;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

public class SweptAxeItem extends WeaponItem {

    public SweptAxeItem(int attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    @Override
    public List<Enchantment> vanillaEnchantments() {
        return Arrays.asList(Enchantments.MOB_LOOTING);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment instanceof DamageEnchantment) return true;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        if (AttackHelper.weaponIsCharged(player)) {

            //Sweep Particle
            MobEffectInstance retaliation = player.getEffect(AllEffects.RETALIATION.get());
            Vec3 eyepos = player.position().add(0, player.getEyeHeight(), 0);
            AttackHelper.makeParticle(target.level, AllParticles.AXE_SWEEP.get(),
                    target.position().add(eyepos).scale(.5),
                    //Axe sweep particle uses xSpeed as scale, ySpeed as being red, zSpeed is horizontal flip
                    .5, (retaliation != null) ? 1 : 0, player.getMainArm() == HumanoidArm.LEFT ? 1 : 0
            );

//            if (player.world.isRemote) {
//                Random rand = player.world.getRandom();
//                AxisAlignedBB bb = target.getBoundingBox();
//                double l = bb.getAverageEdgeLength();
//                Vector3d pos = new Vector3d((rand.nextDouble() - .5) * l, rand.nextDouble() * l + .5, (rand.nextDouble() - .5) * l);
//                AttackHelper.makeParticle(player.world, AllParticles.BUTCHER_SPARK_EMITTER.get(), target.getPositionVec().add(pos));
//            }

            //Used for knockback
            Vec3 look = player.getLookAngle();
            //Calculate damage dealt
            float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            int hits = 1;

            //Get entities to sweep, almost directly copied from sword sweeping code.
            for(LivingEntity livingentity : player.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.5D, 0.5D, 1.5D))) {
                if (livingentity != player && livingentity != target && !player.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker()) && player.distanceToSqr(livingentity) < 12.0D) {
                    //Skip over pets tamed by the player
                    if (livingentity instanceof TamableAnimal) {
                        if (((TamableAnimal) livingentity).isOwnedBy(player)) {
                            continue;
                        }
                    }
                    if (!player.level.isClientSide) {
                        livingentity.knockback(0.4f, -look.x(), -look.z());
                        float bonus = AttackHelper.getBonusEnchantmentDamage(stack, livingentity);
                        if (livingentity.hurt(DamageSource.playerAttack(player), damage + bonus)) {
                            if (bonus > 0) {
                                player.magicCrit(livingentity);
                            }
                        }
                    }
                    hits++;
                }
            }

            if (player.level.isClientSide && hits >= 3) {
                ScreenShaker.setScreenShake(4, 2);
            }


            //Sweep Sound
            AttackHelper.playSound(player, SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, .7f);
        }
        return false;
    }

}
