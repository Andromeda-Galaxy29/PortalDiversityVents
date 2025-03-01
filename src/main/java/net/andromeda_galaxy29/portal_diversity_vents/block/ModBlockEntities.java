package net.andromeda_galaxy29.portal_diversity_vents.block;

import net.andromeda_galaxy29.portal_diversity_vents.PortalDiversityVentsMod;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.vacuum_tube.VacuumTubeBlockEntity;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent.DiversityVentBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PortalDiversityVentsMod.MODID);

    public static final RegistryObject<BlockEntityType<VacuumTubeBlockEntity>> VACUUM_TUBE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("vacuum_tube_block_entity", () ->
                    BlockEntityType.Builder.of(VacuumTubeBlockEntity::new,
                            ModBlocks.VACUUM_TUBE.get()).build(null));

    public static final RegistryObject<BlockEntityType<DiversityVentBlockEntity>> DIVERSITY_VENT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("diversity_vent_block_entity", () ->
                    BlockEntityType.Builder.of(DiversityVentBlockEntity::new,
                            ModBlocks.DIVERSITY_VENT.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
