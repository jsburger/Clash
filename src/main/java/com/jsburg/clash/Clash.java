package com.jsburg.clash;

import com.jsburg.clash.entity.GreatbladeSlashEntity;
import com.jsburg.clash.event.ClientEvents;
import com.jsburg.clash.registry.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("clash")
public class Clash
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "clash";

    public Clash() {

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AllItems.ITEMS.register(modEventBus);
        AllSounds.SOUNDS.register(modEventBus);
        AllParticles.PARTICLE_TYPES.register(modEventBus);
        AllEnchantments.ENCHANTMENTS.register(modEventBus);
        AllEffects.EFFECTS.register(modEventBus);
        MiscRegistry.ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(this::setupCommon);
        modEventBus.addListener(this::setupClient);

    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        // hiiii
    }

    private void setupClient(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ClientEvents::fiddleWithHands);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ClientEvents::doCameraStuff);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, ClientEvents::doClientTick);

        event.enqueueWork(AllItems::registerItemProperties);
        EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
        manager.register(MiscRegistry.GREATBLADE_SLASH.get(), new GreatbladeSlashEntity.SlashRenderer(manager));
        manager.register(MiscRegistry.GREATBLADE_SLASH.get(), new GreatbladeSlashEntity.SlashRenderer(manager));
    }

}
