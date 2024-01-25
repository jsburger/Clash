package com.jsburg.clash.effects;

import com.jsburg.clash.util.MiscHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class StaggerEffect extends MobEffect {
    public StaggerEffect() {
        super(MobEffectCategory.HARMFUL, 16777215);
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.isOnGround()) {
            Vec3 motion = entityLivingBaseIn.getDeltaMovement();
            Vec3 look = MiscHelper.extractHorizontal(entityLivingBaseIn.getViewVector(1)).scale(-1);
            float movespeed = entityLivingBaseIn.getSpeed();
            float speed = (movespeed)/(1 + amplifier);
            double dot = (motion.dot(look));
            if ((dot) < speed) {
                entityLivingBaseIn.setDeltaMovement(motion.add(look.scale(speed - dot)));
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
