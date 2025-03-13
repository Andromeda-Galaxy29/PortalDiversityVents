package net.andromeda_galaxy29.portal_diversity_vents.block.vents.vacuum_tube.junction;

import net.andromeda_galaxy29.portal_diversity_vents.block.vents.vacuum_tube.VacuumTubeBlock;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.vacuum_tube.VacuumTubePart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class VacuumTubeJunctionBlock extends Block {

    public VacuumTubeBlock tubeReplacement;

    public static final BooleanProperty NORTH;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty EAST;
    public static final BooleanProperty DOWN;
    public static final BooleanProperty UP;

    public VacuumTubeJunctionBlock(Properties properties, @NotNull VacuumTubeBlock tubeReplacement){
        super(properties);
        this.registerDefaultState(defaultBlockState()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(DOWN, false)
                .setValue(UP, false));

        this.tubeReplacement = tubeReplacement;
        tubeReplacement.junctionReplacement = this;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, SOUTH, WEST, EAST, DOWN, UP});
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
        VoxelShape shapeNorth = Block.box(0, 0, -8, 16, 16, -7.99);
        VoxelShape shapeSouth = Block.box(0, 0, 23.99, 16, 16, 24);
        VoxelShape shapeWest = Block.box(-8, 0, 0, -7.99, 16, 16);
        VoxelShape shapeEast = Block.box(23.99, 0, 0, 24, 16, 16);
        VoxelShape shapeDown = Block.box(0, -8, 0, 16, -7.99, 16);
        VoxelShape shapeUp = Block.box(0, 23.99, 0, 16, 24, 16);

        VoxelShape shape = Shapes.empty();
        if (!(state.getValue(NORTH))){
            shape = Shapes.or(shape, shapeNorth);
        }
        if (!(state.getValue(SOUTH))){
            shape = Shapes.or(shape, shapeSouth);
        }
        if (!(state.getValue(WEST))){
            shape = Shapes.or(shape, shapeWest);
        }
        if (!(state.getValue(EAST))){
            shape = Shapes.or(shape, shapeEast);
        }
        if (!(state.getValue(DOWN))){
            shape = Shapes.or(shape, shapeDown);
        }
        if (!(state.getValue(UP))){
            shape = Shapes.or(shape, shapeUp);
        }
        return shape;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean p_60514_) {
        super.neighborChanged(state, level, pos, block, neighborPos, p_60514_);
        boolean north = level.getBlockState(pos.relative(Direction.NORTH)).getBlock() == tubeReplacement;
        boolean south = level.getBlockState(pos.relative(Direction.SOUTH)).getBlock() == tubeReplacement;
        boolean west = level.getBlockState(pos.relative(Direction.WEST)).getBlock() == tubeReplacement;
        boolean east = level.getBlockState(pos.relative(Direction.EAST)).getBlock() == tubeReplacement;
        boolean down = level.getBlockState(pos.relative(Direction.DOWN)).getBlock() == tubeReplacement;
        boolean up = level.getBlockState(pos.relative(Direction.UP)).getBlock() == tubeReplacement;
        int connections = (north?1:0) + (south?1:0) + (west?1:0) + (east?1:0) + (down?1:0) + (up?1:0);

        if (connections == 2){
            if((north && south) || (west && east) || (down && up)){
                level.setBlockAndUpdate(pos, tubeReplacement.defaultBlockState());
            }
        }else if (connections == 1){
            level.setBlockAndUpdate(pos, tubeReplacement.defaultBlockState());
        }

        level.setBlockAndUpdate(pos, state
                .setValue(NORTH, north)
                .setValue(SOUTH, south)
                .setValue(WEST, west)
                .setValue(EAST, east)
                .setValue(DOWN, down)
                .setValue(UP, up));
    }

    static {
        NORTH = BooleanProperty.create("north");
        SOUTH = BooleanProperty.create("south");
        WEST = BooleanProperty.create("west");
        EAST = BooleanProperty.create("east");
        DOWN = BooleanProperty.create("down");
        UP = BooleanProperty.create("up");
    }
}
