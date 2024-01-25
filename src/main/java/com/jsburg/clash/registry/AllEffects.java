package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.effects.RampageEffect;
import com.jsburg.clash.effects.RetaliationEffect;
import com.jsburg.clash.effects.StaggerEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Clash.MOD_ID);

    public static final RegistryObject<MobEffect> RAMPAGING = EFFECTS.register("rampaging", RampageEffect::new);
    public static final RegistryObject<MobEffect> RETALIATION = EFFECTS.register("retaliation", RetaliationEffect::new);
    public static final RegistryObject<MobEffect> STAGGERED = EFFECTS.register("staggered", StaggerEffect::new);

}
