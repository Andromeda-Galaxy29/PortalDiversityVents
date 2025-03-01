package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent;

import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlockEntities;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlock;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DiversityVentBlockEntity extends PipeBlockEntity {

    public DiversityVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIVERSITY_VENT_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);

        DiversityVentDirection ventDir = state.getValue(DiversityVentBlock.DIRECTION);

        if (ventDir == DiversityVentDirection.NONE){
            return;
        }

        Direction dir = Direction.fromAxisAndDirection(state.getValue(PipeBlock.AXIS), ventDir.getAxisDirection());

        suck(level, pos, dir);
    }

    public void suck(Level level, BlockPos pos, Direction dir){
        if(!(level.getBlockState(pos.relative(dir, -1)).getBlock() instanceof AirBlock)){
            return;
        }

        Vec3 inPos = pos.getCenter().relative(dir, -0.5);
        Vec3 sideOffset = new Vec3(
                dir.getAxis().choose(0, 1, 1),
                dir.getAxis().choose(1, 0, 1),
                dir.getAxis().choose(1, 1, 0));

        AABB bounding_box = new AABB(
                inPos.add(sideOffset),
                inPos.relative(dir, -4).subtract(sideOffset));

        List<Entity> entities = level.getEntities(null, bounding_box);
        for (Entity entity : entities){
            if (!canTransport(entity) || entity.isShiftKeyDown()){
                continue;
            }

            if (entity instanceof Player){
                entity.hurtMarked = true; //Needs to be set or else players are unaffected
            }

            Vec3 push = pos.getCenter().subtract(entity.getBoundingBox().getCenter()).normalize().scale(0.2);
            entity.push(push.x, push.y, push.z);
            entity.resetFallDistance();
        }
    }

    public void createParticles(Level level, BlockPos pos, Direction.Axis axis, DiversityVentDirection ventDir){
        //TODO: Particles
    }

    @Override
    public Direction getMotionDirection(Level level, Entity entity, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof DiversityVentBlock)){
            return null;
        }

        Direction.Axis axis = state.getValue(PipeBlock.AXIS);
        DiversityVentDirection ventDir = state.getValue(DiversityVentBlock.DIRECTION);
        if(ventDir == DiversityVentDirection.NONE){
            return super.getMotionDirection(level, entity, pos, state);
        }else{
            return Direction.fromAxisAndDirection(axis, ventDir.getAxisDirection());
        }
    }
}
