package com.jsburg.clash.registry;

import com.jsburg.clash.Clash;
import com.jsburg.clash.rendering.GreatbladeRenderer;
import com.jsburg.clash.weapons.BillhookItem;
import com.jsburg.clash.weapons.GreatbladeItem;
import com.jsburg.clash.weapons.SpearItem;
import com.jsburg.clash.weapons.SweptAxeItem;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public class AllItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Clash.MOD_ID);

    public static final RegistryObject<Item> SPEAR = ITEMS.register("spear", () -> new SpearItem(5, 2.0F, (new Item.Properties()).maxDamage(852)));
    public static final RegistryObject<Item> BILLHOOK = ITEMS.register("billhook", () -> new BillhookItem(5, 2.0F, (new Item.Properties()).maxDamage(852)));
    public static final RegistryObject<Item> SWEPT_AXE = ITEMS.register("swept_axe", () -> new SweptAxeItem(6, 0.7F, (new Item.Properties()).maxDamage(450)));
    public static final RegistryObject<Item> SWEPT_AXE_HEAD = ITEMS.register("swept_axe_head", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC)));
    public static final RegistryObject<Item> LOST_GREATBLADE = ITEMS.register("lost_greatblade", () -> new GreatbladeItem(5, 0.6F,
            (new Item.Properties()).maxDamage(512).setISTER(() -> GreatbladeRenderer::getInstance)));

    public static void registerItemProperties() {
//        ItemModelsProperties.registerProperty(LOST_GREATBLADE.get(), new ResourceLocation(Clash.MOD_ID, "in_hand"),
//                (itemStack, world, entity) ->
//                        entity != null && (entity.getHeldItemMainhand() == itemStack || entity.getHeldItemOffhand() == itemStack) ? 1 : 0
//                );
    }

}
