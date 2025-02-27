package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent_gate;

import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlockEntities;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DiversityVentGateBlock extends PipeBlock {

    public static final EnumProperty<DiversityVentGateDirection> DIRECTION;

    public DiversityVentGateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Z).setValue(DIRECTION, DiversityVentGateDirection.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS, DIRECTION});
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return new DiversityVentGateBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()){
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.DIVERSITY_VENT_GATE_BLOCK_ENTITY.get(),
                (level1, pos, state1, blockEntity) -> blockEntity.tick(level1, pos, state1));
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.getItemInHand(hand).getItem() != Items.AIR){
            return InteractionResult.PASS;
        }

        DiversityVentGateDirection current = state.getValue(DIRECTION);
        for (int i = 0; i < DiversityVentGateDirection.values().length; i++){
            if (DiversityVentGateDirection.values()[i] == current){
                int next = i + 1;
                if (next >= DiversityVentGateDirection.values().length){
                    next = 0;
                }
                level.setBlockAndUpdate(pos, state.setValue(DIRECTION, DiversityVentGateDirection.values()[next]));
                break;
            }
        }
        player.swing(hand);
        return InteractionResult.SUCCESS;
    }

    static {
        DIRECTION = EnumProperty.create("direction", DiversityVentGateDirection.class);
    }
}
