package moe.plushie.rpgeconomy.core.client.gui.controls;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiTabPanel<T extends GuiScreen> extends Gui {
    
    protected static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(LibGuiResources.BACKGROUND);
    protected static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.BUTTONS);
    protected static final ResourceLocation TEXTURE_ICONS = new ResourceLocation(LibGuiResources.ICONS);
    
    private final int tabId;
    protected final T parent;
    protected final FontRenderer fontRenderer;
    protected final Minecraft mc;
    
    protected ArrayList<GuiButton> buttonList;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private final boolean fullscreen;
    private GuiButton selectedButton;
    
    protected AbstractGuiDialog dialog;
    int oldMouseX;
    int oldMouseY;
    
    public GuiTabPanel(int tabId, T parent, boolean fullscreen) {
        this.tabId = tabId;
        this.parent = parent;
        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = mc.fontRenderer;
        this.fullscreen = fullscreen;
        
        buttonList = new ArrayList<GuiButton>();
    }
    
    public void initGui(int xPos, int yPos, int width, int height) {
        buttonList.clear();
        if (fullscreen) {
            this.x = 0;
            this.y = 0;
            this.width = parent.width;
            this.height = parent.height;
        } else {
            this.x = xPos;
            this.y = yPos;
            this.width = width;
            this.height = height;
        }
        if (isDialogOpen()) {
            dialog.initGui();
        }
    }
    
    public int getTabId() {
        return tabId;
    }
    
    public void tabChanged(int tabIndex) {
        
    }
    
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!isDialogOpen()) {
            if (button == 0) {
                for (int i = 0; i < buttonList.size(); i++) {
                    GuiButton guiButton = buttonList.get(i);
                    if (guiButton.mousePressed(mc, mouseX, mouseY)) {
                        ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(parent, guiButton, buttonList);
                        if (MinecraftForge.EVENT_BUS.post(event)) {
                            break;
                        }
                        this.selectedButton = event.getButton();
                        event.getButton().playPressSound(this.mc.getSoundHandler());
                        this.actionPerformed(event.getButton());
                        if (parent.equals(this.mc.currentScreen)) {
                            MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(parent, event.getButton(), this.buttonList));
                        }
                        return true;
                    }
                }
            }
        } else {
            dialog.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }
    
    protected void actionPerformed(GuiButton button) {}
    
    public boolean mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (!isDialogOpen()) {
            if (this.selectedButton != null && button == 0) {
                this.selectedButton.mouseReleased(mouseX - x, mouseY - y);
                this.selectedButton = null;
            }
        } else {
            dialog.mouseMovedOrUp(mouseX, mouseY, button);
        }
        return false;
    }
    
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        oldMouseX = mouseX;
        oldMouseY = mouseY;
        if (isDialogOpen()) {
            mouseX = mouseY = 0;
        }
        for (int i = 0; i < buttonList.size(); i++) {
            buttonList.get(i).drawButton(mc, mouseX - x, mouseY - y, partialTickTime);
        }
    }
    
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        if (isDialogOpen()) {
            //GL11.glTranslatef(-parent.width, -y, 0);
            dialog.draw(mouseX, mouseX, partialTickTime);
            //GL11.glTranslatef(x, y, 0);
        }
    }
    
    public boolean keyTyped(char c, int keycode) {
        if (isDialogOpen()) {
            return dialog.keyTyped(c, keycode);
        }
        return false;
    }
    
    public void openDialog(AbstractGuiDialog dialog) {
        this.dialog = dialog;
        dialog.initGui();
    }

    public boolean isDialogOpen() {
        return dialog != null;
    }
    
    protected void closeDialog() {
        this.dialog = null;
    }
}
