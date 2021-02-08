package com.jsburg.clash.weapons;

import com.jsburg.clash.Clash;
import com.jsburg.clash.registry.AllSounds;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpearItem extends WeaponItem {

    public SpearItem(int attackDamage, float attackSpeed, Item.Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void onHit(ItemStack stack, PlayerEntity player, Entity target) {
//        if (AttackHelper.weaponIsCharged(player)) {
//            AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_STAB.get());
//        }
    }
}
