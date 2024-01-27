package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.registry.AllEffects;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        //Set retaliation to be removed after it has been used.
        Player player = event.getEntity();
        Entity target = event.getTarget();
        MobEffectInstance retaliation = player.getEffect(AllEffects.RETALIATION.get());
        if (retaliation != null) {
            retaliation.duration = 1;

            //Spawn screen shake particle
            Vec3 eyepos = player.position().add(0, player.getEyeHeight(), 0);
            AttackHelper.makeParticle(target.level, AllParticles.SCREEN_SHAKER.get(),
                    target.position().add(eyepos).scale(.5),
                    //Intensity base, Shake duration, Intensity falloff distance
                    (1 + retaliation.getAmplifier()) * .5, 2 + retaliation.getAmplifier() * 2, 12
            );

        }
    }
}
