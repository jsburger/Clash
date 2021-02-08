package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Clash.MOD_ID);

    private static RegistryObject<SoundEvent> register(String key) {
        return SOUNDS.register(key, () -> new SoundEvent(new ResourceLocation(Clash.MOD_ID, key)));
    }

    public static final RegistryObject<SoundEvent> WEAPON_SPEAR_STAB = register("weapon.spear.stab");

}
