package net.andromeda_galaxy29.portal_diversity_vents.item.wrench;

import net.andromeda_galaxy29.portal_diversity_vents.block.WrenchableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(player.isShiftKeyDown() && player.getItemInHand(hand).hasTag()){
            player.getItemInHand(hand).setTag(new CompoundTag());
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (level.getBlockEntity(pos) instanceof WrenchableBlockEntity){
            return ((WrenchableBlockEntity) level.getBlockEntity(pos)).onWrenched(state, context);
        }
        return super.useOn(context);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag();
    }

    public void connect(ItemStack stack, BlockPos pos, Direction dir){
        CompoundTag tag = new CompoundTag();
        tag.put("ConnectPos", NbtUtils.writeBlockPos(pos));
        tag.putString("ConnectDir", dir.getName());
        stack.setTag(tag);
    }
}
