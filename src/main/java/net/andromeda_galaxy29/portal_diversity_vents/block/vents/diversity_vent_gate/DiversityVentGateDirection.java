package net.andromeda_galaxy29.portal_diversity_vents.block.vents.diversity_vent_gate;

import net.minecraft.util.StringRepresentable;

public enum DiversityVentGateDirection implements StringRepresentable {
    POSITIVE,
    NEGATIVE,
    NONE;

    private DiversityVentGateDirection() {
    }

    public String toString() {
        return this.getSerializedName();
    }

    public String getSerializedName() {
        String toReturn = "none";
        switch (this){
            case POSITIVE -> {toReturn = "positive";}
            case NEGATIVE -> {toReturn = "negative";}
            case NONE -> {toReturn = "none";}
        }
        return toReturn;
    }
}
