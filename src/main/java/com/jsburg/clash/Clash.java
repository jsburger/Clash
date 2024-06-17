package com.jsburg.clash;

import com.jsburg.clash.event.ClientEvents;
import com.jsburg.clash.registry.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("clash")
public class Clash
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "clash";
    public static ResourceLocation rl(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
    public IEventBus eventBus;
    public Clash(IEventBus modEventBus, ModContainer modContainer) {

        this.eventBus = modEventBus;

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);

        AllItems.ITEMS.register(modEventBus);
        AllSounds.SOUNDS.register(modEventBus);
        AllParticles.PARTICLE_TYPES.register(modEventBus);
        //AllEnchantments.ENCHANTMENTS.register(modEventBus);
        //AllEffects.EFFECTS.register(modEventBus);
        //MiscRegistry.ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(this::setupClient);

    }

    private void setupClient(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ClientEvents::fiddleWithHands);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ClientEvents::doCameraStuff);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, ClientEvents::doClientTick);
        eventBus.addListener(EventPriority.LOW, AllParticles::registerParticleFactories);

        event.enqueueWork(AllItems::registerItemProperties);
        EntityRenderers.register(MiscRegistry.GREATBLADE_SLASH.get(), NoopRenderer::new);
        EntityRenderers.register(MiscRegistry.GREATBLADE_SLASH_EXECUTIONER.get(), NoopRenderer::new);
    }

}
