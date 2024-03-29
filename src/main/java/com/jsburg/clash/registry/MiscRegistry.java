package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.entity.GreatbladeSlashEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class MiscRegistry {

    // TAGS
    public static final ITag<EntityType<?>> PORKY = makeEntityTypeTag("porky_entities");

    private static ITag<EntityType<?>> makeEntityTypeTag(String id) {
        return EntityTypeTags.getTagById(Clash.MOD_ID + ":" + id);
    }

    //ENTITY TYPES
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Clash.MOD_ID);

    public static final RegistryObject<EntityType<Entity>> GREATBLADE_SLASH = registerEntityType("greatblade_slash",
            EntityType.Builder.create(GreatbladeSlashEntity::new, EntityClassification.MISC)
                    .size(3, 3).trackingRange(4).updateInterval(20).immuneToFire().disableSummoning()
    );
    public static final RegistryObject<EntityType<Entity>> GREATBLADE_SLASH_EXECUTIONER = registerEntityType("greatblade_slash_executioner",
            EntityType.Builder.create(GreatbladeSlashEntity::new, EntityClassification.MISC)
                    //im doing this just to make it smaller. IDK what to do otherwise.
                    .size(1, 3).trackingRange(4).updateInterval(20).immuneToFire().disableSummoning()
    );

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(String id, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(id, () -> builder.build(Clash.MOD_ID + ":" + id));
    }

}
