package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Clash.MOD_ID);

    private static DeferredHolder<SoundEvent, ? extends SoundEvent> register(String key) {
        return SOUNDS.register(key, () -> SoundEvent.createVariableRangeEvent(Clash.rl(key)));
    }

    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> WEAPON_SPEAR_STAB = register("weapon.spear.stab");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> WEAPON_SPEAR_WHOOSH = register("weapon.spear.whoosh");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> WEAPON_SPEAR_MEGA_CRIT = register("weapon.spear.mega_crit");

}
