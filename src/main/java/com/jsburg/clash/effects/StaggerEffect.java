package com.jsburg.clash.effects;

import com.jsburg.clash.util.MiscHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.vector.Vector3d;

public class StaggerEffect extends Effect {
    public StaggerEffect() {
        super(EffectType.HARMFUL, 16777215);
    }

    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.isOnGround()) {
            Vector3d motion = entityLivingBaseIn.getMotion();
            Vector3d look = MiscHelper.extractHorizontal(entityLivingBaseIn.getLook(1)).scale(-1);
            float movespeed = entityLivingBaseIn.getAIMoveSpeed();
            float speed = (movespeed)/(1 + amplifier);
            double dot = (motion.dotProduct(look));
            if ((dot) < speed) {
                entityLivingBaseIn.setMotion(motion.add(look.scale(speed - dot)));
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
}
