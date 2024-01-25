package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.entity.GreatbladeSlashEntity;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class MiscRegistry {

    // TAGS
    public static final Tag<EntityType<?>> PORKY = makeEntityTypeTag("porky_entities");

    private static Tag<EntityType<?>> makeEntityTypeTag(String id) {
        return EntityTypeTags.bind(Clash.MOD_ID + ":" + id);
    }

    //ENTITY TYPES
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Clash.MOD_ID);

    public static final RegistryObject<EntityType<Entity>> GREATBLADE_SLASH = registerEntityType("greatblade_slash",
            EntityType.Builder.of(GreatbladeSlashEntity::new, MobCategory.MISC)
                    .sized(3, 3).clientTrackingRange(4).updateInterval(20).fireImmune().noSummon()
    );
    public static final RegistryObject<EntityType<Entity>> GREATBLADE_SLASH_EXECUTIONER = registerEntityType("greatblade_slash_executioner",
            EntityType.Builder.of(GreatbladeSlashEntity::new, MobCategory.MISC)
                    //im doing this just to make it smaller. IDK what to do otherwise.
                    .sized(1, 3).clientTrackingRange(4).updateInterval(20).fireImmune().noSummon()
    );

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(String id, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(id, () -> builder.build(Clash.MOD_ID + ":" + id));
    }

}
