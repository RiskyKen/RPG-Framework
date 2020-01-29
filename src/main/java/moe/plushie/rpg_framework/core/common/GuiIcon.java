package moe.plushie.rpg_framework.core.common;

import moe.plushie.rpg_framework.api.core.IGuiIcon;

public class GuiIcon implements IGuiIcon {

    private final String[] classPaths;
    private final AnchorHorizontal anchorHorizontal;
    private final AnchorVertical anchorVertical;
    private final int offsetHorizontal;
    private final int offsetVertical;
    private final int iconIndex;
    private final float iconAlpha;

    public GuiIcon(String[] classPaths, AnchorHorizontal anchorHorizontal, AnchorVertical anchorVertical, int offsetHorizontal, int offsetVertical, int iconIndex, float iconAlpha) {
        this.classPaths = classPaths;
        this.anchorHorizontal = anchorHorizontal;
        this.anchorVertical = anchorVertical;
        this.offsetHorizontal = offsetHorizontal;
        this.offsetVertical = offsetVertical;
        this.iconIndex = iconIndex;
        this.iconAlpha = iconAlpha;
    }

    @Override
    public String[] getClassPaths() {
        return classPaths;
    }

    @Override
    public AnchorHorizontal getAnchorHorizontal() {
        return anchorHorizontal;
    }

    @Override
    public AnchorVertical getAnchorVertical() {
        return anchorVertical;
    }

    @Override
    public int getOffsetHorizontal() {
        return offsetHorizontal;
    }

    @Override
    public int getOffsetVertical() {
        return offsetVertical;
    }

    @Override
    public int getIconIndex() {
        return iconIndex;
    }

    @Override
    public float getIconAlpha() {
        return iconAlpha;
    }
}
