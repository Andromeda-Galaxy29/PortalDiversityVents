package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent;

import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlockEntities;
import net.andromeda_galaxy29.portal_diversity_vents.block.WrenchableBlockEntity;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlock;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiversityVentBlockEntity extends PipeBlockEntity implements WrenchableBlockEntity {

    public DiversityVentDirection ventDirection = DiversityVentDirection.NONE;
    public DiversityVentFilter filter = DiversityVentFilter.IGNORE_SHIFT;

    public DiversityVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIVERSITY_VENT_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public @NotNull InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (!(state.getBlock() instanceof DiversityVentBlock)) {
            return InteractionResult.PASS;
        }

        if(context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()){
            for (int i = 0; i < DiversityVentDirection.values().length; i++){
                if (DiversityVentDirection.values()[i] == ventDirection){
                    int next = i + 1;
                    if (next >= DiversityVentDirection.values().length){
                        next = 0;
                    }

                    ventDirection = DiversityVentDirection.values()[next];
                    context.getLevel().setBlockAndUpdate(
                            context.getClickedPos(), state.setValue(DiversityVentBlock.DIRECTION, ventDirection));
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if(context.getPlayer() != null && context.getPlayer().isShiftKeyDown()){
            for (int i = 0; i < DiversityVentFilter.values().length; i++){
                if (DiversityVentFilter.values()[i] == filter){
                    int next = i + 1;
                    if (next >= DiversityVentFilter.values().length){
                        next = 0;
                    }

                    filter = DiversityVentFilter.values()[next];
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);

        if (ventDirection == DiversityVentDirection.NONE){
            return;
        }

        Direction dir = Direction.fromAxisAndDirection(state.getValue(PipeBlock.AXIS), ventDirection.getAxisDirection());

        suck(level, pos, dir);
        createParticles(level, pos, state.getValue(PipeBlock.AXIS), ventDirection);
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
            if (!canTransport(entity)){
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
        if(level.isClientSide){
            return;
        }
        ServerLevel serverLevel = ((ServerLevel) level);
    }

    @Override
    public Direction getMotionDirection(Level level, Entity entity, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof DiversityVentBlock)){
            return null;
        }

        Direction.Axis axis = state.getValue(PipeBlock.AXIS);
        if(ventDirection == DiversityVentDirection.NONE){
            return super.getMotionDirection(level, entity, pos, state);
        }else{
            return Direction.fromAxisAndDirection(axis, ventDirection.getAxisDirection());
        }
    }

    @Override
    public boolean canTransport(Entity entity) {
        if(filter == DiversityVentFilter.ACCEPT_ALL) {
            return super.canTransport(entity);
        }else if(filter == DiversityVentFilter.IGNORE_SHIFT) {
            return super.canTransport(entity) && !entity.isShiftKeyDown();
        }else if(filter == DiversityVentFilter.IGNORE_PLAYERS){
            return super.canTransport(entity) && !(entity instanceof Player);
        }
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putString("Direction", ventDirection.getSerializedName());
        tag.putString("Filter", filter.getSerializedName());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        ventDirection = DiversityVentDirection.valueOf(tag.getString("Direction").toUpperCase());
        filter = DiversityVentFilter.valueOf(tag.getString("Filter").toUpperCase());
        super.load(tag);
    }
}
