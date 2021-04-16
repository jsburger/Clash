package com.jsburg.clash.weapons;

import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;

public class SweptAxeItem extends WeaponItem {

    public SweptAxeItem(int attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }


    @Override
    public void onHit(ItemStack stack, PlayerEntity player, Entity target) {
        if (AttackHelper.weaponIsCharged(player)) {
            AttackHelper.makeParticle(target.world, AllParticles.AXE_SWEEP.get(), target.getPositionVec().add(player.getPositionVec()).scale(.5).add(0, .5, 0), Vector3d.ZERO, 0);
        }
    }

}
