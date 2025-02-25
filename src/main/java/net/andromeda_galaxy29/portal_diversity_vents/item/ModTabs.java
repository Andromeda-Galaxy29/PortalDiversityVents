package net.andromeda_galaxy29.portal_diversity_vents.item;

import net.andromeda_galaxy29.portal_diversity_vents.PortalDiversityVentsMod;
import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PortalDiversityVentsMod.MODID);

    public static final RegistryObject<CreativeModeTab> TAB_DIVERSITY_VENTS = TABS.register("diversity_vents", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModBlocks.DIVERSITY_VENT.get()))
            .title(Component.translatable("creativetab.diversity_vents"))
            .displayItems((itemDisplayParameters, output) -> {
                output.accept(ModBlocks.DIVERSITY_VENT.get());
                output.accept(ModBlocks.DIVERSITY_VENT_GATE.get());
            })
            .build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}