package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum DiversityVentFilter implements StringRepresentable {
    ACCEPT_ALL,
    IGNORE_SHIFT,
    IGNORE_PLAYERS;

    private DiversityVentFilter() {
    }

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {
        if (this == IGNORE_SHIFT) {
            return "ignore_shift";
        }else if (this == IGNORE_PLAYERS){
            return "ignore_players";
        }else {
            return "accept_all";
        }
    }
}
