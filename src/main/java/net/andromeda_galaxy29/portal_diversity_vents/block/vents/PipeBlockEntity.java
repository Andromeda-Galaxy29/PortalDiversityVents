package net.andromeda_galaxy29.portal_diversity_vents.block.vents;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class PipeBlockEntity extends BlockEntity {

    public PipeBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof PipeBlock)){
            return;
        }

        List<Entity> entities = level.getEntities(null, new AABB(pos)); //TODO: Better detection?
        for (Entity entity : entities){

            Direction motionDirection = getMotionDirection(level, entity, pos, state);
            if (motionDirection == null){
                continue;
            }

            if (!canTransport(entity)){
                continue;
            }

            if (motionDirection.getAxis() == state.getValue(PipeBlock.AXIS)){
                if (entity instanceof Player){
                    entity.hurtMarked = true; //Needs to be set or else players are unaffected
                }

                Vec3 push = pos.relative(motionDirection, 2).getCenter()
                        .subtract(entity.getBoundingBox().getCenter()).normalize().scale(0.7);
                entity.hasImpulse = true;
                entity.setDeltaMovement(push);
                entity.resetFallDistance();
            }
        }
    }

    public boolean canTransport(Entity entity){
        return entity.getBoundingBox().getXsize() < 2 &&
                entity.getBoundingBox().getYsize() < 2 &&
                entity.getBoundingBox().getZsize() < 2;
    }

    public @Nullable Direction getMotionDirection(Level level, Entity entity, BlockPos pos, BlockState state){
        double x = entity.getDeltaMovement().x();
        double y = entity.getDeltaMovement().y();
        double z = entity.getDeltaMovement().z();
        return Direction.getNearest(x, y, z);
    }
}
