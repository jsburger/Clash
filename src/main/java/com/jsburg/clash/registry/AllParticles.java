package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Clash.MOD_ID);

    public static final RegistryObject<SimpleParticleType> SPEAR_STAB = register("spear_stab");
    public static final RegistryObject<SimpleParticleType> SPEAR_CRIT = register("spear_crit");
    public static final RegistryObject<SimpleParticleType> DASH_DUST = register("dash_dust", false);

    public static final RegistryObject<SimpleParticleType> AXE_SWEEP = register("axe_sweep");
    public static final RegistryObject<SimpleParticleType> BUTCHER_SPARK = register("butcher_spark", false);
    public static final RegistryObject<SimpleParticleType> BUTCHER_SPARK_EMITTER = register("butcher_spark_emitter");

    public static final RegistryObject<SimpleParticleType> BONUS_DROP = register("bonus_drop");
    public static final RegistryObject<SimpleParticleType> SCREEN_SHAKER = register("screen_shaker");

    public static final RegistryObject<SimpleParticleType> SAILING_TRAIL = register("sailing_trail");
    public static final RegistryObject<SimpleParticleType> GREATBLADE_SLASH = register("greatblade_slash_2");

    //Called from ClientEvents
    public static void registerParticleFactories() {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        manager.register(SPEAR_STAB.get(), SpearStabParticle.Factory::new);
        manager.register(SPEAR_CRIT.get(), SpearCritParticle.Factory::new);
        manager.register(DASH_DUST.get(), DashDustParticle.Factory::new);

        manager.register(AXE_SWEEP.get(), AxeSweepParticle.Factory::new);
        manager.register(BUTCHER_SPARK.get(), ClashSpriteParticle::ButcherSpark);
        manager.register(BUTCHER_SPARK_EMITTER.get(), ButcherSparkEmitter.Factory::new);

        manager.register(BONUS_DROP.get(), ClashSpriteParticle::BonusDrop);
        manager.register(SCREEN_SHAKER.get(), ScreenShakerParticle.Factory::new);

        manager.register(SAILING_TRAIL.get(), ClashSpriteParticle::SailingTrail);
        manager.register(GREATBLADE_SLASH.get(), AxeSweepParticle.BladeFactory::new);
    }

    private static RegistryObject<SimpleParticleType> register(String name) {
        return register(name, true);
    }

    private static RegistryObject<SimpleParticleType> register(String name, boolean alwaysShow) {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(alwaysShow));
    }



}
