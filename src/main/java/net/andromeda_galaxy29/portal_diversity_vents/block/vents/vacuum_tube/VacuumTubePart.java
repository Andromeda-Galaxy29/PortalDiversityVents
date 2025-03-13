package net.andromeda_galaxy29.portal_diversity_vents.block.vents.vacuum_tube;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum VacuumTubePart implements StringRepresentable {
    DEFAULT,
    FRONT,
    MIDDLE,
    JUNCTION_PART,
    BACK;

    private VacuumTubePart() {
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
        }else if (this == JUNCTION_PART) {
            return "junction_part";
        }else {
            return "default";
        }
    }
}
