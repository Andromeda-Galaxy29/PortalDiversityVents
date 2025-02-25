package net.andromeda_galaxy29.portal_diversity_vents.block.vents;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class PipeBlock extends Block{

    public static final EnumProperty<Direction.Axis> AXIS;

    public PipeBlock(Properties properties){
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Z));
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS});
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

    static {
        AXIS = BlockStateProperties.AXIS;
    }
}
