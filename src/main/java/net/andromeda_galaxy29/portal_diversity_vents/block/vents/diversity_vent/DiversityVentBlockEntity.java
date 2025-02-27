package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent;

import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlockEntities;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DiversityVentBlockEntity extends BlockEntity {

    public DiversityVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIVERSITY_VENT_BLOCK_ENTITY.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof DiversityVentBlock)){
            return;
        }

        List<Entity> entities = level.getEntities(null, new AABB(pos));
        for (var entity : entities){
            double x = entity.getDeltaMovement().x();
            double y = entity.getDeltaMovement().y();
            double z = entity.getDeltaMovement().z();
            Direction entityMotionDirection = Direction.getNearest(x, y, z);
            if (entityMotionDirection.getAxis() == state.getValue(PipeBlock.AXIS)){
                if (entity instanceof Player){
                    entity.hurtMarked = true;
                }

                Vec3 push = pos.relative(entityMotionDirection, 2).getCenter()
                        .subtract(entity.getBoundingBox().getCenter()).normalize();
                entity.setDeltaMovement(push);
                entity.resetFallDistance();
            }
        }
    }
}
