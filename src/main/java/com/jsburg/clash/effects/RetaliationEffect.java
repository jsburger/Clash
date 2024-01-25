package com.jsburg.clash.effects;

import net.minecraft.world.effect.AttackDamageMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RetaliationEffect extends AttackDamageMobEffect {

    public RetaliationEffect() {
        super(MobEffectCategory.BENEFICIAL, 0, 2);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "648D7065-6A61-4F60-8ABE-C2C23A6DD7B0", 0.0D, AttributeModifier.Operation.ADDITION);
    }

}
