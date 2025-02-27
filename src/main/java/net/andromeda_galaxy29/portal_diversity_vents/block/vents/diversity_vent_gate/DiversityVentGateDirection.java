package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent_gate;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum DiversityVentGateDirection implements StringRepresentable {
    POSITIVE,
    NEGATIVE,
    NONE;

    private DiversityVentGateDirection() {
    }

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {
        if (this == POSITIVE) {
            return "positive";
        }else if (this == NEGATIVE){
            return "negative";
        }else {
            return "none";
        }
    }

    public Direction.AxisDirection getAxisDirection(){
        if (this == POSITIVE) {
            return Direction.AxisDirection.POSITIVE;
        }else if (this == NEGATIVE){
            return Direction.AxisDirection.NEGATIVE;
        }else {
            return null;
        }
    }

    public int toInt(){
        if (this == POSITIVE) {
            return 1;
        }else if (this == NEGATIVE){
            return -1;
        }else {
            return 0;
        }
    }
}
