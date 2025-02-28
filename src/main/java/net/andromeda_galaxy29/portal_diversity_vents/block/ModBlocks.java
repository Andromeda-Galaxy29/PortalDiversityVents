package net.andromeda_galaxy29.portal_diversity_vents.block;

import net.andromeda_galaxy29.portal_diversity_vents.PortalDiversityVentsMod;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent.VacuumTubeBlock;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent_gate.DiversityVentBlock;
import net.andromeda_galaxy29.portal_diversity_vents.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PortalDiversityVentsMod.MODID);

    public static final RegistryObject<Block> VACUUM_TUBE = registerBlock("vacuum_tube",
            () -> new VacuumTubeBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).noOcclusion()));
    public static final RegistryObject<Block> DIVERSITY_VENT = registerBlock("diversity_vent",
            () -> new DiversityVentBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, Supplier<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
