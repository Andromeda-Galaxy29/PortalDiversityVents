package net.andromeda_galaxy29.portal_diversity_vents.block.vents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DiversityVentBlock extends Block {

    public static final EnumProperty<Direction.Axis> AXIS;
    public static final EnumProperty<DiversityVentPart> PART;

    public DiversityVentBlock(Properties properties){
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Z).setValue(PART, DiversityVentPart.DEFAULT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS, PART});
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean p_60514_) {
        super.neighborChanged(state, level, pos, block, neighborPos, p_60514_);
        BlockPos frontPos = pos.relative(state.getValue(AXIS), -1);
        BlockState frontState = level.getBlockState(frontPos);
        BlockPos backPos = pos.relative(state.getValue(AXIS), 1);
        BlockState backState = level.getBlockState(backPos);

        if (!canConnect(level, pos, state, frontPos, frontState) && canConnect(level, pos, state, backPos, backState)){
            level.setBlockAndUpdate(pos, state.setValue(PART, DiversityVentPart.FRONT));
        }else if (canConnect(level, pos, state, frontPos, frontState) && canConnect(level, pos, state, backPos, backState)){
            level.setBlockAndUpdate(pos, state.setValue(PART, DiversityVentPart.MIDDLE));
        }else if (canConnect(level, pos, state, frontPos, frontState) && !canConnect(level, pos, state, backPos, backState)){
            level.setBlockAndUpdate(pos, state.setValue(PART, DiversityVentPart.BACK));
        }else{
            level.setBlockAndUpdate(pos, state.setValue(PART, DiversityVentPart.DEFAULT));
        }
    }

    public boolean canConnect(Level level, BlockPos pos, BlockState state, BlockPos otherPos, BlockState otherState){
        if (!(otherState.getBlock() instanceof DiversityVentBlock &&
                state.getValue(AXIS) == otherState.getValue(AXIS))) {
            return false;
        }

        //Connect only by 3 vents
        //Don't connect if the other one is already maximum length
        BlockPos behindOtherPos = pos.offset(otherPos.subtract(pos).multiply(2));
        BlockState behindOtherState = level.getBlockState(behindOtherPos);
        if(behindOtherState.getBlock() instanceof DiversityVentBlock && behindOtherState.getValue(PART) == DiversityVentPart.MIDDLE){
            return false;
        }
        //Don't connect if this one is already maximum length
        BlockPos behindPos = pos.offset(pos.subtract(otherPos));
        BlockState behindState = level.getBlockState(behindPos);
        if(behindState.getBlock() instanceof DiversityVentBlock && behindState.getValue(PART) == DiversityVentPart.MIDDLE){
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
        AXIS = BlockStateProperties.AXIS;
        PART = EnumProperty.create("part", DiversityVentPart.class);
    }
}
