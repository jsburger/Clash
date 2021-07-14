package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Clash.MOD_ID);

    public static final RegistryObject<BasicParticleType> SPEAR_STAB = PARTICLE_TYPES.register("spear_stab", () ->
            new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> SPEAR_CRIT = PARTICLE_TYPES.register("spear_crit", () ->
            new BasicParticleType(true));
    
    public static final RegistryObject<BasicParticleType> AXE_SWEEP = PARTICLE_TYPES.register("axe_sweep", () ->
            new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> BUTCHER_SPARK = PARTICLE_TYPES.register("butcher_spark", () ->
            new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> BUTCHER_SPARK_EMITTER = PARTICLE_TYPES.register("butcher_spark_emitter", () ->
            new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> DAST_DUST = PARTICLE_TYPES.register("dash_dust", () ->
            new BasicParticleType(true));

}
