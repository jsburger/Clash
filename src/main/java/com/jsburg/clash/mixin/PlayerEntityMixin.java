package com.jsburg.clash.mixin;

import com.jsburg.clash.weapons.GreatbladeItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

//@Mixin(PlayerEntity.class)
//public abstract class PlayerEntityMixin extends LivingEntity implements GreatbladeItem.IGreatbladeUser {
//
//    @Unique
//    private int clash_greatbladeSwingTime = 0;
//
//    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {
//        super(type, worldIn);
//    }
//
//    @Override
//    public int getSwingTime() {
//        return clash_greatbladeSwingTime;
//    }
//
//    @Override
//    public void incrementSwingTime() {
//        clash_greatbladeSwingTime++;
//    }
//
//    @Override
//    public void resetSwingTime() {
//        clash_greatbladeSwingTime = 0;
//    }
//}
