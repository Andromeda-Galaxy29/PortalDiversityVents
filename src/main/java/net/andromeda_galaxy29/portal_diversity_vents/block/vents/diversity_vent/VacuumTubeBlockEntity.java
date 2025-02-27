package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent;

import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlockEntities;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VacuumTubeBlockEntity extends PipeBlockEntity {

    public VacuumTubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VACUUM_TUBE_BLOCK_ENTITY.get(), pos, state);
    }
}
