package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent;

import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlockEntities;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DiversityVentBlockEntity extends PipeBlockEntity {

    public DiversityVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIVERSITY_VENT_BLOCK_ENTITY.get(), pos, state);
    }
}
