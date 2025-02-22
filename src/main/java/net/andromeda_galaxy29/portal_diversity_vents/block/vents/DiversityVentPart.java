package net.andromeda_galaxy29.portal_diversity_vents.block.vents;

import net.minecraft.util.StringRepresentable;

public enum DiversityVentPart implements StringRepresentable {
    DEFAULT,
    FRONT,
    MIDDLE,
    BACK;

    private DiversityVentPart() {
    }

    public String toString() {
        return this.getSerializedName();
    }

    public String getSerializedName() {
        String toReturn = "default";
        switch (this){
            case DEFAULT -> {toReturn = "default";}
            case FRONT -> {toReturn = "front";}
            case MIDDLE -> {toReturn = "middle";}
            case BACK -> {toReturn = "back";}
        }
        return toReturn;
    }
}
