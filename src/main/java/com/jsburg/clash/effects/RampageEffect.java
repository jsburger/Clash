package com.jsburg.clash.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class RampageEffect extends Effect {

    public RampageEffect() {
        super(EffectType.BENEFICIAL, 0);
    }

    @Override
    public void performEffect(LivingEntity target, int amplifier) {
        // Uses attack ticks because attack speed only reduces the tick requirement of an attack,
        // meaning if it runs out, progress gained on a swing is reverted
        if (target instanceof PlayerEntity) {
            target.ticksSinceLastSwing += amplifier + 1;
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 2 == 0;
    }
}
