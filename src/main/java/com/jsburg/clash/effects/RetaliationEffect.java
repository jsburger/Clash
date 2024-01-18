package com.jsburg.clash.effects;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.AttackDamageEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class RetaliationEffect extends AttackDamageEffect {

    public RetaliationEffect() {
        super(EffectType.BENEFICIAL, 0, 2);
        this.addAttributesModifier(Attributes.ATTACK_DAMAGE, "648D7065-6A61-4F60-8ABE-C2C23A6DD7B0", 0.0D, AttributeModifier.Operation.ADDITION);
    }

}
