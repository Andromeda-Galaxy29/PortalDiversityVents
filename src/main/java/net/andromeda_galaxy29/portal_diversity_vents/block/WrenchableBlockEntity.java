package net.andromeda_galaxy29.portal_diversity_vents.block;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface WrenchableBlockEntity {
    default @NotNull InteractionResult onWrenched(BlockState state, UseOnContext context){
        return InteractionResult.PASS;
    }
}
