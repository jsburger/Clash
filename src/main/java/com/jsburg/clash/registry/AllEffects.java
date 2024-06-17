package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.effects.RampageEffect;
import com.jsburg.clash.effects.RetaliationEffect;
import com.jsburg.clash.effects.StaggerEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Clash.MOD_ID);

    public static final DeferredHolder<MobEffect, RampageEffect> RAMPAGING = EFFECTS.register("rampaging", RampageEffect::new);
    public static final DeferredHolder<MobEffect, RetaliationEffect> RETALIATION = EFFECTS.register("retaliation", RetaliationEffect::new);
    public static final DeferredHolder<MobEffect, StaggerEffect> STAGGERED = EFFECTS.register("staggered", StaggerEffect::new);

}
