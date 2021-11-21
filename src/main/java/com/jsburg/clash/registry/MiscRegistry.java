package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;


public class MiscRegistry {

    public static final ITag<EntityType<?>> PORKY = makeEntityTypeTag("porky_entities");

    private static ITag<EntityType<?>> makeEntityTypeTag(String id) {
        return EntityTypeTags.getTagById(Clash.MOD_ID + ":" + id);
    }
}
