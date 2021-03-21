package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.particle.SpearCritParticle;
import com.jsburg.clash.particle.SpearStabParticle;
import com.jsburg.clash.registry.AllParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        ParticleManager manager = Minecraft.getInstance().particles;
        manager.registerFactory(AllParticles.SPEAR_STAB.get(), SpearStabParticle.Factory::new);
        manager.registerFactory(AllParticles.SPEAR_CRIT.get(), SpearCritParticle.Factory::new);
    }

}
