package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum DiversityVentPart implements StringRepresentable {
    DEFAULT,
    FRONT,
    MIDDLE,
    BACK;

    private DiversityVentPart() {
    }

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {
        if (this == FRONT){
            return "front";
        }else if (this == MIDDLE){
            return "middle";
        }else if (this == BACK){
            return "back";
        }else {
            return "default";
        }
    }
}
