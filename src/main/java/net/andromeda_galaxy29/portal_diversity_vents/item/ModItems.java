package net.andromeda_galaxy29.portal_diversity_vents.item;

import net.andromeda_galaxy29.portal_diversity_vents.PortalDiversityVentsMod;
import net.andromeda_galaxy29.portal_diversity_vents.item.wrench.WrenchItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PortalDiversityVentsMod.MODID);

    public static final RegistryObject<Item> WRENCH = ITEMS.register("pipe_wrenching_tool",
            () -> new WrenchItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
