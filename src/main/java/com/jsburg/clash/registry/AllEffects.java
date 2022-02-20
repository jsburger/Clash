package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.effects.RampageEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllEffects {

    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, Clash.MOD_ID);

    public static final RegistryObject<Effect> RAMPAGING = EFFECTS.register("rampaging", RampageEffect::new);
}
