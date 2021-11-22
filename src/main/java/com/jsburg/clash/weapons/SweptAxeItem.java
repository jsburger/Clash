package com.jsburg.clash.weapons;

import com.jsburg.clash.event.ClientEvents;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.util.ScreenShaker;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class SweptAxeItem extends WeaponItem {

    public SweptAxeItem(int attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }


    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity target) {
        if (AttackHelper.weaponIsCharged(player)) {

            //Sweep Particle
            Vector3d eyepos = player.getPositionVec().add(0, player.getEyeHeight(), 0);
            AttackHelper.makeParticle(target.world, AllParticles.AXE_SWEEP.get(),
                    target.getPositionVec().add(eyepos).scale(.5),
                    Vector3d.ZERO, 0
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
                        livingentity.attackEntityFrom(DamageSource.causePlayerDamage(player), damage + AttackHelper.getBonusEnchantmentDamage(stack, livingentity));
                    }
                    hits++;
                }
            }

            if (player.world.isRemote && hits >= 3) {
                ScreenShaker.setScreenShake(5, .8);
            }


            //Sweep Sound
            AttackHelper.playSound(player, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, .7f);
        }
        return false;
    }

}
