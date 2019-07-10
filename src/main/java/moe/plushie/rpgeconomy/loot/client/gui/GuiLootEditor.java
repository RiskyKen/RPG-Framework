package moe.plushie.rpgeconomy.loot.client.gui;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTab;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.loot.common.inventory.ContainerLootEditor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLootEditor extends GuiTabbed {

    private static int activeTab = 0;

    public final GuiTabLootTableEditor tabLootTableEditor;
    public final GuiTabLootPoolEditor tabLootPoolEditor;

    public GuiLootEditor(EntityPlayer player) {
        super(new ContainerLootEditor(player), false);

        tabLootTableEditor = new GuiTabLootTableEditor(0, this);
        tabLootPoolEditor = new GuiTabLootPoolEditor(1, this);

        tabList.add(tabLootTableEditor);
        tabList.add(tabLootPoolEditor);

        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.loot_tables.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.loot_pools.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));

        tabController.setActiveTabIndex(getActiveTab());
        tabController.setTabsPerSide(9);

        tabChanged();
    }

    @Override
    public void initGui() {
        this.xSize = 320;
        this.ySize = 240;
        super.initGui();
    }

    @Override
    protected int getActiveTab() {
        return activeTab;
    }

    @Override
    protected void setActiveTab(int value) {
        this.activeTab = value;
    }

    @Override
    public String getName() {
        return "loot_editor";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        for (GuiTabPanel tabPanel : tabList) {
            if (tabPanel.getTabId() == activeTab) {
                tabPanel.drawBackgroundLayer(partialTicks, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        for (GuiTabPanel tabPanel : tabList) {
            if (tabPanel.getTabId() == activeTab) {
                tabPanel.drawForegroundLayer(mouseX, mouseY, 0);
            }
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        GL11.glPopMatrix();
    }
}
