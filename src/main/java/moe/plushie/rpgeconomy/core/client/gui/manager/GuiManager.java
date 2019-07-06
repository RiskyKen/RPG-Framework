package moe.plushie.rpgeconomy.core.client.gui.manager;

import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTab;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.core.common.inventory.ContainerManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiManager extends GuiTabbed {

    private static final String GUI_NAME = "manager";

    private static int activeTab;

    private final EntityPlayer player;
    private final GuiManagerTabMain tabMain;

    public GuiManager(EntityPlayer player) {
        super(new ContainerManager(player), false);
        this.player = player;

        tabMain = new GuiManagerTabMain(0, this);
        
        tabList.add(tabMain);

        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.main.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));

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
        return GUI_NAME;
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
    }
}
