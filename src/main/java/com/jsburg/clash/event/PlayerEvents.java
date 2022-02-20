package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.registry.AllEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        //Set retaliation to be removed after it has been used.
        PlayerEntity player = event.getPlayer();
        EffectInstance retaliation = player.getActivePotionEffect(AllEffects.RETALIATION.get());
        if (retaliation != null) {
            retaliation.duration = 1;
        }
    }
}
