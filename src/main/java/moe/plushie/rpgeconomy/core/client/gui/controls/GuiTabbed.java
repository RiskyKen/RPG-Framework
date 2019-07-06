package moe.plushie.rpgeconomy.core.client.gui.controls;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiTabbed extends GuiContainer {

    protected GuiTabController tabController;
    protected ArrayList<GuiTabPanel> tabList;

    public GuiTabbed(Container container, boolean fullscreen) {
        super(container);
        tabController = new GuiTabController(this, fullscreen);
        tabList = new ArrayList<GuiTabPanel>();
    }

    protected abstract int getActiveTab();

    protected abstract void setActiveTab(int value);

    public abstract String getName();

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        tabController.initGui(getGuiLeft() + 4, guiTop, xSize, ySize);

        tabController.setActiveTabIndex(getActiveTab());

        for (int i = 0; i < tabList.size(); i++) {
            tabList.get(i).initGui(getGuiLeft() + 21, getGuiTop(), xSize - 42, ySize);
        }
        buttonList.add(tabController);

        tabChanged();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    protected void tabChanged() {
        setActiveTab(tabController.getActiveTabIndex());
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            tab.tabChanged(getActiveTab());
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        boolean clicked = false;
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                if (tab.mouseClicked(mouseX, mouseY, button)) {
                    clicked = true;
                }
            }
        }
        if (!clicked) {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        boolean clicked = false;
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                if (tab.mouseMovedOrUp(mouseX, mouseY, state)) {
                    clicked = true;
                }
            }
        }
        if (!clicked) {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == tabController) {
            tabChanged();
        }
    }

    @Override
    protected void keyTyped(char c, int keycode) throws IOException {
        boolean keyTyped = false;
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                keyTyped = tab.keyTyped(c, keycode);
            }
        }
        if (!keyTyped) {
            super.keyTyped(c, keycode);
        }
    }
}
