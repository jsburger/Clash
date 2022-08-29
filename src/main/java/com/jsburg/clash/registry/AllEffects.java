package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.effects.RampageEffect;
import com.jsburg.clash.effects.RetaliationEffect;
import com.jsburg.clash.effects.StaggerEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllEffects {

    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, Clash.MOD_ID);

    public static final RegistryObject<Effect> RAMPAGING = EFFECTS.register("rampaging", RampageEffect::new);
    public static final RegistryObject<Effect> RETALIATION = EFFECTS.register("retaliation", RetaliationEffect::new);
    public static final RegistryObject<Effect> STAGGERED = EFFECTS.register("staggered", StaggerEffect::new);

}
