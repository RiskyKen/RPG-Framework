package moe.plushie.rpg_framework.core.client.gui.controls;

import java.util.ArrayList;

import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiControl extends Gui {

    public static boolean debugDrawMargin = false;
    public static boolean debugDrawBorder = false;
    public static boolean debugDrawPadding = false;

    private int x;
    private int y;
    protected int width;
    protected int height;

    protected int marginLeft, marginRight, marginTop, marginBottom = 0;
    protected int paddingLeft, paddingRight, paddingTop, paddingBottom = 0;

    protected GuiControl activeControl = null;
    protected ArrayList<GuiControl> controls = new ArrayList<GuiControl>();

    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getLeft() {
        return marginLeft + x + paddingLeft;
    }
    
    public int getRight() {
        return x + width - marginRight - paddingRight;
    }
    
    public int getTop() {
        return marginTop + y + paddingTop;
    }
    
    public int getBottom() {
        return y + height - marginBottom - paddingBottom;
    }
    
    public void parentResized(GuiControl parent) {
        parentResized(parent.getLeft(), parent.getTop(), parent.getRight(), parent.getBottom());
    }

    public void parentResized(int left, int top, int right, int bottom) {
        this.x = left;
        this.y = top;
        this.width = right - left;
        this.height = bottom - top;
        for (GuiControl control : controls) {
            control.parentResized(this);
        }
    }

    public void draw(int x, int y, int mouseX, int mouseY, float partialTicks) {
        for (GuiControl control : controls) {
            control.draw(x, y, mouseX, mouseY, partialTicks);
        }
    }

    public void drawHover(int x, int y, int mouseX, int mouseY, float partialTicks) {
        for (GuiControl control : controls) {
            control.drawHover(x, y, mouseX, mouseY, partialTicks);
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        boolean clicked = false;
        for (GuiControl control : controls) {
            if (control.mouseClicked(mouseX, mouseY, button) & button == 0) {
                activeControl = control;
                clicked = true;
            }
        }
        return clicked;
    }

    public void mouseDrag(int mouseX, int mouseY, int button, long timeSinceClick) {
        for (GuiControl control : controls) {
            control.mouseDrag(mouseX, mouseY, button, timeSinceClick);
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int button) {
        if (activeControl != null && button == 0) {
            for (GuiControl control : controls) {
                control.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    public boolean keyTyped(char c, int keyCode) {
        boolean typed = false;
        for (GuiControl control : controls) {
            if (control.keyTyped(c, keyCode)) {
                typed = true;
            }
        }
        return typed;
    }

    public GuiControl setPadding(int left, int right, int top, int bottom) {
        this.paddingLeft = left;
        this.paddingRight = right;
        this.paddingTop = top;
        this.paddingBottom = bottom;
        return this;
    }

    public GuiControl setMargin(int left, int right, int top, int bottom) {
        this.marginLeft = left;
        this.marginRight = right;
        this.marginTop = top;
        this.marginBottom = bottom;
        return this;
    }
}
