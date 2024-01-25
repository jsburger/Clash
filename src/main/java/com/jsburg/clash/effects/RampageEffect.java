package com.jsburg.clash.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class RampageEffect extends MobEffect {

    public RampageEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public void applyEffectTick(LivingEntity target, int amplifier) {
        // Uses attack ticks because attack speed only reduces the tick requirement of an attack,
        // meaning if it runs out, progress gained on a swing is reverted
        if (target instanceof Player) {
            target.attackStrengthTicker += amplifier + 1;
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        //Reduce the frequency of the effect going off, reducing the effectiveness while keeping it an int.
        return duration % 2 == 0;
    }
}
