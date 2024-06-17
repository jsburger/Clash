package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, Clash.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPEAR_STAB = register("spear_stab");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPEAR_CRIT = register("spear_crit");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DASH_DUST = register("dash_dust", false);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> AXE_SWEEP = register("axe_sweep");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BUTCHER_SPARK = register("butcher_spark", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BUTCHER_SPARK_EMITTER = register("butcher_spark_emitter");

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BONUS_DROP = register("bonus_drop");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SCREEN_SHAKER = register("screen_shaker");

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SAILING_TRAIL = register("sailing_trail");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GREATBLADE_SLASH = register("greatblade_slash_2");

    //Registered in Clash Client Setup
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        event.registerSpriteSet(SPEAR_STAB.get(), SpearStabParticle.Factory::new);
        event.registerSpriteSet(SPEAR_CRIT.get(), SpearCritParticle.Factory::new);
        event.registerSpriteSet(DASH_DUST.get(), DashDustParticle.Factory::new);

        event.registerSpriteSet(AXE_SWEEP.get(), AxeSweepParticle.Factory::new);
        event.registerSpriteSet(BUTCHER_SPARK.get(), ClashSpriteParticle::ButcherSpark);
        event.registerSpriteSet(BUTCHER_SPARK_EMITTER.get(), ButcherSparkEmitter.Factory::new);

        event.registerSpriteSet(BONUS_DROP.get(), ClashSpriteParticle::BonusDrop);
        event.registerSpriteSet(SCREEN_SHAKER.get(), ScreenShakerParticle.Factory::new);

        event.registerSpriteSet(SAILING_TRAIL.get(), ClashSpriteParticle::SailingTrail);
        event.registerSpriteSet(GREATBLADE_SLASH.get(), AxeSweepParticle.BladeFactory::new);
    }

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String name) {
        return register(name, true);
    }

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String name, boolean alwaysShow) {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(alwaysShow));
    }



}
