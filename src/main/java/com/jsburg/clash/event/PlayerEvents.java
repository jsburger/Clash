package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.registry.AllEffects;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        //Set retaliation to be removed after it has been used.
        PlayerEntity player = event.getPlayer();
        Entity target = event.getTarget();
        EffectInstance retaliation = player.getActivePotionEffect(AllEffects.RETALIATION.get());
        if (retaliation != null) {
            retaliation.duration = 1;

            //Spawn screen shake particle
            Vector3d eyepos = player.getPositionVec().add(0, player.getEyeHeight(), 0);
            AttackHelper.makeParticle(target.world, AllParticles.SCREEN_SHAKER.get(),
                    target.getPositionVec().add(eyepos).scale(.5),
                    //Intensity base, Shake duration, Intensity falloff distance
                    (1 + retaliation.getAmplifier()) * .5, 2 + retaliation.getAmplifier() * 2, 12
            );

        }
    }
}
