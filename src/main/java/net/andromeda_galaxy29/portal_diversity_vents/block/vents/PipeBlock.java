package net.andromeda_galaxy29.portal_diversity_vents.block.vents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PipeBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction.Axis> AXIS;

    public PipeBlock(Properties properties){
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Z));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS});
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
        VoxelShape shapeX = Block.box(-8, 0, 0, 24, 16, 16);
        VoxelShape shapeY = Block.box(0, -8, 0, 16, 24, 16);
        VoxelShape shapeZ = Block.box(0, 0, -8, 16, 16, 24);

        if (state.getValue(AXIS) == Direction.Axis.X){
            return Shapes.or(shapeY, shapeZ);
        }else if (state.getValue(AXIS) == Direction.Axis.Y){
            return Shapes.or(shapeX, shapeZ);
        }else if (state.getValue(AXIS) == Direction.Axis.Z){
            return Shapes.or(shapeY, shapeX);
        }
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
        VoxelShape shapeNorth = Block.box(0, 0, -8, 16, 16, -7.99);
        VoxelShape shapeSouth = Block.box(0, 0, 23.99, 16, 16, 24);
        VoxelShape shapeWest = Block.box(-8, 0, 0, -7.99, 16, 16);
        VoxelShape shapeEast = Block.box(23.99, 0, 0, 24, 16, 16);
        VoxelShape shapeDown = Block.box(0, -8, 0, 16, -7.99, 16);
        VoxelShape shapeUp = Block.box(0, 23.99, 0, 16, 24, 16);

        if (state.getValue(AXIS) == Direction.Axis.X){
            return Shapes.or(shapeDown, shapeUp, shapeNorth, shapeSouth);
        }else if (state.getValue(AXIS) == Direction.Axis.Y){
            return Shapes.or(shapeWest, shapeEast, shapeNorth, shapeSouth);
        }else if (state.getValue(AXIS) == Direction.Axis.Z){
            return Shapes.or(shapeDown, shapeUp, shapeWest, shapeEast);
        }
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!(context.getPlayer() != null && context.getPlayer().isShiftKeyDown())){
            for (Direction dir : Direction.values()){
                BlockState state = context.getLevel().getBlockState(context.getClickedPos().relative(dir));
                if (state.getBlock() instanceof PipeBlock && dir.getAxis() == state.getValue(AXIS)){
                    return defaultBlockState().setValue(AXIS, state.getValue(AXIS));
                }
            }
        }
        return defaultBlockState().setValue(AXIS, context.getNearestLookingDirection().getAxis());
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.MODEL;
    }

    static {
        AXIS = BlockStateProperties.AXIS;
    }
}
