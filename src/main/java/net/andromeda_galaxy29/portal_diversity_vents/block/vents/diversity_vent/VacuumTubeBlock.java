package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent;

import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlockEntities;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class VacuumTubeBlock extends PipeBlock {

    public static final EnumProperty<VacuumTubePart> PART;

    public VacuumTubeBlock(Properties properties){
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Z).setValue(PART, VacuumTubePart.DEFAULT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS, PART});
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return new VacuumTubeBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()){
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.VACUUM_TUBE_BLOCK_ENTITY.get(),
                (level1, pos, state1, blockEntity) -> blockEntity.tick(level1, pos, state1));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean p_60514_) {
        super.neighborChanged(state, level, pos, block, neighborPos, p_60514_);
        BlockPos frontPos = pos.relative(state.getValue(AXIS), 1);
        BlockState frontState = level.getBlockState(frontPos);
        BlockPos backPos = pos.relative(state.getValue(AXIS), -1);
        BlockState backState = level.getBlockState(backPos);

        if (!canConnect(level, pos, state, frontPos, frontState) && canConnect(level, pos, state, backPos, backState)){
            level.setBlockAndUpdate(pos, state.setValue(PART, VacuumTubePart.FRONT));
        }else if (canConnect(level, pos, state, frontPos, frontState) && canConnect(level, pos, state, backPos, backState)){
            level.setBlockAndUpdate(pos, state.setValue(PART, VacuumTubePart.MIDDLE));
        }else if (canConnect(level, pos, state, frontPos, frontState) && !canConnect(level, pos, state, backPos, backState)){
            level.setBlockAndUpdate(pos, state.setValue(PART, VacuumTubePart.BACK));
        }else{
            level.setBlockAndUpdate(pos, state.setValue(PART, VacuumTubePart.DEFAULT));
        }
    }

    public boolean canConnect(Level level, BlockPos pos, BlockState state, BlockPos otherPos, BlockState otherState){
        if (!(otherState.getBlock() instanceof VacuumTubeBlock &&
                state.getValue(AXIS) == otherState.getValue(AXIS))) {
            return false;
        }

        //Connect only by 3 vents
        //Don't connect if the other one is already maximum length
        BlockPos behindOtherPos = pos.offset(otherPos.subtract(pos).multiply(2));
        BlockState behindOtherState = level.getBlockState(behindOtherPos);
        if(behindOtherState.getBlock() instanceof VacuumTubeBlock &&
                state.getValue(AXIS) == behindOtherState.getValue(AXIS) &&
                behindOtherState.getValue(PART) == VacuumTubePart.MIDDLE){
            return false;
        }
        //Don't connect if this one is already maximum length
        BlockPos behindPos = pos.offset(pos.subtract(otherPos));
        BlockState behindState = level.getBlockState(behindPos);
        if (behindState.getBlock() instanceof VacuumTubeBlock &&
                state.getValue(AXIS) == behindState.getValue(AXIS) &&
                behindState.getValue(PART) == VacuumTubePart.MIDDLE){
            return false;
        }

        return true;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }

    @Override
    public boolean skipRendering(BlockState state1, BlockState state2, Direction direction) {
        return state2.is(this) ? true : super.skipRendering(state1, state2, direction);
    }

    static {
        PART = EnumProperty.create("part", VacuumTubePart.class);
    }
}
