package com.jsburg.clash.weapons;

import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SweptAxeItem extends WeaponItem {

    public SweptAxeItem(int attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }


    @Override
    public void onHit(ItemStack stack, PlayerEntity player, Entity target) {
        if (AttackHelper.weaponIsCharged(player)) {
            //Sweep Particle
            AttackHelper.makeParticle(target.world, AllParticles.AXE_SWEEP.get(),
                    target.getPositionVec().add(player.getPositionVec()).scale(.5).add(0, .5, 0),
                    Vector3d.ZERO, 0
            );
            //Used for knockback
            Vector3d look = player.getLookVec();
            //Calculate damage dealt
            float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);

            //Sweep get entities to sweep, almost directly copied from sword sweeping code.
            for(LivingEntity livingentity : player.world.getEntitiesWithinAABB(LivingEntity.class, target.getBoundingBox().grow(1.25D, 0.5D, 1.25D))) {
                if (livingentity != player && livingentity != target && !player.isOnSameTeam(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).hasMarker()) && player.getDistanceSq(livingentity) < 12.0D) {
                    livingentity.applyKnockback(0.4f, -look.getX(), -look.getZ());
                    livingentity.attackEntityFrom(DamageSource.causePlayerDamage(player), damage + AttackHelper.getBonusEnchantmentDamage(stack, livingentity));
                }
            }

            //Sweep Sound
            AttackHelper.playSound(player, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, .7f);
        }
    }

}