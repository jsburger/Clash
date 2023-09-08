package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Clash.MOD_ID);

    public static final RegistryObject<BasicParticleType> SPEAR_STAB = register("spear_stab");
    public static final RegistryObject<BasicParticleType> SPEAR_CRIT = register("spear_crit");
    public static final RegistryObject<BasicParticleType> DASH_DUST = register("dash_dust", false);

    public static final RegistryObject<BasicParticleType> AXE_SWEEP = register("axe_sweep");
    public static final RegistryObject<BasicParticleType> BUTCHER_SPARK = register("butcher_spark", false);
    public static final RegistryObject<BasicParticleType> BUTCHER_SPARK_EMITTER = register("butcher_spark_emitter");

    public static final RegistryObject<BasicParticleType> BONUS_DROP = register("bonus_drop");
    public static final RegistryObject<BasicParticleType> SCREEN_SHAKER = register("screen_shaker");

    public static final RegistryObject<BasicParticleType> SAILING_TRAIL = register("sailing_trail");
    public static final RegistryObject<BasicParticleType> GREATBLADE_SLASH = register("greatblade_slash");

    //Called from ClientEvents
    public static void registerParticleFactories() {
        ParticleManager manager = Minecraft.getInstance().particles;
        manager.registerFactory(SPEAR_STAB.get(), SpearStabParticle.Factory::new);
        manager.registerFactory(SPEAR_CRIT.get(), SpearCritParticle.Factory::new);
        manager.registerFactory(DASH_DUST.get(), DashDustParticle.Factory::new);

        manager.registerFactory(AXE_SWEEP.get(), AxeSweepParticle.Factory::new);
        manager.registerFactory(BUTCHER_SPARK.get(), ClashSpriteParticle::ButcherSpark);
        manager.registerFactory(BUTCHER_SPARK_EMITTER.get(), ButcherSparkEmitter.Factory::new);

        manager.registerFactory(BONUS_DROP.get(), ClashSpriteParticle::BonusDrop);
        manager.registerFactory(SCREEN_SHAKER.get(), ScreenShakerParticle.Factory::new);

        manager.registerFactory(SAILING_TRAIL.get(), ClashSpriteParticle::SailingTrail);
        manager.registerFactory(GREATBLADE_SLASH.get(), AxeSweepParticle.Factory::new);
    }

    private static RegistryObject<BasicParticleType> register(String name) {
        return register(name, true);
    }

    private static RegistryObject<BasicParticleType> register(String name, boolean alwaysShow) {
        return PARTICLE_TYPES.register(name, () -> new BasicParticleType(alwaysShow));
    }



}
