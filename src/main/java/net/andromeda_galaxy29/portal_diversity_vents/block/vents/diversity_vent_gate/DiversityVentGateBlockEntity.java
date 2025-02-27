package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent_gate;

import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlockEntities;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlock;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DiversityVentGateBlockEntity extends PipeBlockEntity {

    public DiversityVentGateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIVERSITY_VENT_GATE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);

        Direction.Axis axis = state.getValue(PipeBlock.AXIS);
        DiversityVentGateDirection ventDir = state.getValue(DiversityVentGateBlock.DIRECTION);

        if (ventDir == DiversityVentGateDirection.NONE){
            return;
        }

        suck(level, pos, axis, ventDir);
    }

    public void suck(Level level, BlockPos pos, Direction.Axis axis, DiversityVentGateDirection ventDir){
        if(level.getBlockState(pos.relative(axis, -ventDir.toInt())).getBlock() instanceof PipeBlock){
            return;
        }

        Vec3 sideOffset = new Vec3(axis.choose(0, 1.5, 1.5), axis.choose(1.5, 0, 1.5), axis.choose(1.5, 1.5, 0));
        AABB bounding_box = new AABB(
                pos.relative(axis, -ventDir.toInt()).getCenter().add(sideOffset),
                pos.relative(axis, -ventDir.toInt() * 4).getCenter().subtract(sideOffset));

        List<Entity> entities = level.getEntities(null, bounding_box);
        for (var entity : entities){
            if (!canTransport(entity)){
                continue;
            }

            if (entity instanceof Player){
                entity.hurtMarked = true; //Needs to be set or else players are unaffected
            }

            Vec3 push = pos.getCenter().subtract(entity.getBoundingBox().getCenter()).normalize().scale(0.3);
            entity.push(push.x, push.y, push.z);
            entity.resetFallDistance();
        }
    }

    public void createParticles(Level level, BlockPos pos, Direction.Axis axis, DiversityVentGateDirection ventDir){
        level.addParticle(new DustParticleOptions(Vec3.fromRGB24(16433664).toVector3f(), 1), pos.getX(), pos.getY(), pos.getZ(), 0,0,0);
    }

    @Override
    public Direction getMotionDirection(Level level, Entity entity, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof DiversityVentGateBlock)){
            return null;
        }

        Direction.Axis axis = state.getValue(PipeBlock.AXIS);
        DiversityVentGateDirection ventDir = state.getValue(DiversityVentGateBlock.DIRECTION);
        if(ventDir == DiversityVentGateDirection.NONE){
            return super.getMotionDirection(level, entity, pos, state);
        }else{
            return Direction.fromAxisAndDirection(axis, ventDir.getAxisDirection());
        }
    }
}
