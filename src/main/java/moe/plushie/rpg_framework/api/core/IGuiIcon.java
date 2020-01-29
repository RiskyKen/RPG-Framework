package moe.plushie.rpg_framework.api.core;

public interface IGuiIcon {

    public String[] getClassPaths();

    public AnchorHorizontal getAnchorHorizontal();

    public AnchorVertical getAnchorVertical();

    public int getOffsetHorizontal();

    public int getOffsetVertical();

    public int getIconIndex();

    public float getIconAlpha();

    public enum AnchorHorizontal {
        LEFT, CENTER, RIGHT
    }

    public enum AnchorVertical {
        TOP, CENTER, BOTTOM
    }
}
