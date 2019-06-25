package moe.plushie.rpgeconomy.core.client.gui.controls;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabController extends GuiButtonExt {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.TABS);
    private static final ResourceLocation ICONS = new ResourceLocation(LibGuiResources.ICONS);
    
    private GuiScreen parent;
    private boolean fullscreen;
    private int activeTab = -1;
    private ArrayList<GuiTab> tabs = new ArrayList<GuiTab>();
    private int tabSpacing = 27;
    private boolean editMode = false;
    
    public GuiTabController(GuiScreen parent, boolean fullscreen, int xPos, int yPos, int width, int height) {
        super(0, xPos, yPos, width, height, "");
        this.parent = parent;
        this.fullscreen = fullscreen;
        if (!fullscreen) {
            tabSpacing = 25;
        }
    }
    
    public GuiTabController(GuiScreen parent, boolean fullscreen) {
        this(parent, fullscreen, 0, 0, 0, 0);
    }
    
    public void setTabSpacing(int tabSpacing) {
        this.tabSpacing = tabSpacing;
    }
    
    public void initGui(int xPos, int yPos, int width, int height) {
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
    }
    
    public void setActiveTabIndex(int index) {
        if (index < getTabCount() - 1) {
            activeTab = index;
        } else {
            activeTab = getTabCount() - 1;
        }
        if (getTabCount() == 0) {
            activeTab = -1;
        }
    }
    
    public void addTab(GuiTab tab) {
        tabs.add(tab);
    }
    
    public void clearTabs() {
        tabs.clear();
    }
    
    public int getTabCount() {
        return tabs.size();
    }
    
    public int getActiveTabIndex() {
        return activeTab;
    }
    
    public String getActiveTabName() {
        GuiTab tab = getActiveTab();
        if (tab != null) {
            return tab.getName();
        }
        return "";
    }
    
    public GuiTab getTab(int index) {
        if (index >= 0 & index < tabs.size()) {
            return tabs.get(index);
        }
        return null;
    }
    
    public GuiTab getActiveTab() {
        if (activeTab >= 0 & activeTab < tabs.size()) {
            return tabs.get(activeTab);
        }
        return null;
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        int yOffset = (int) ((float)height / 2F - ((float)tabs.size() * tabSpacing) / 2F);
        if (!fullscreen) {
            yOffset = 5;
        }
        int count = 0;
        for (int i = 0; i < tabs.size(); i++) {
            GuiTab tab = tabs.get(i);
            if (tab.visible) {
                if (tab.isMouseOver(this.x - 4, this.y + count * tabSpacing  + yOffset, mouseX, mouseY)) {
                    if (tab.mousePress(this.x - 4, this.y + count * tabSpacing  + yOffset, mouseX, mouseY)) {
                        activeTab = i;
                    }
                    return true;
                }
                count++;
            }
        }
        return false;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        mc.renderEngine.bindTexture(TEXTURE);
        GL11.glColor4f(1, 1, 1, 1);
        int yOffset = (int) ((float)height / 2F - ((float)tabs.size() * tabSpacing) / 2F);
        
        if (!fullscreen) {
            yOffset = 5;
        }
        
        int count = 0;
        for (int i = 0; i < tabs.size(); i++) {
            GuiTab tab = tabs.get(i);
            if (tab.visible) {
                mc.renderEngine.bindTexture(TEXTURE);
                tab.render(i, this.x - 4, this.y + count * tabSpacing + yOffset, mouseX, mouseY, activeTab == i, ICONS);
                count++;
            }
        }
    }
    
    public boolean isEditMode() {
        return editMode;
    }
    
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
    
    public void drawHoverText(Minecraft mc, int mouseX, int mouseY) {
        int yOffset = (int) ((float)height / 2F - ((float)tabs.size() * tabSpacing) / 2F);
        
        if (!fullscreen) {
            yOffset = 5;
        }
        
        GuiTab hoverTab = null;
        int count = 0;
        for (int i = 0; i < tabs.size(); i++) {
            GuiTab tab = tabs.get(i);
            if (tab.visible) {
                if (tab.isMouseOver(this.x - 4, this.y + count * tabSpacing + yOffset, mouseX, mouseY)) {
                    hoverTab = tab;
                }
                count++;
            }
        }
        
        if (hoverTab != null) {
            ArrayList<String> textList = new ArrayList<String>();
            textList.add(hoverTab.getName());
            GuiHelper.drawHoveringText(textList, mouseX, mouseY, mc.fontRenderer, parent.width, parent.height, zLevel);
        }
    }
    
    public static interface ITabEditCallback {
        
        public void tabAdded();
        
        public void tabRemoved(int index);
        
        public void tabMovedBack(int index);
        
        public void tabMovedForward(int index);
    }
}
