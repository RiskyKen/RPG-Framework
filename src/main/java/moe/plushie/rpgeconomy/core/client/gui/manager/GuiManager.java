package moe.plushie.rpgeconomy.core.client.gui.manager;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTab;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.core.common.inventory.ContainerManager;
import moe.plushie.rpgeconomy.loot.client.gui.GuiLootTabPoolEditor;
import moe.plushie.rpgeconomy.loot.client.gui.GuiLootTabTableEditor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiManager extends GuiTabbed {

    private static final String GUI_NAME = "manager";

    private static int activeTab;

    private final EntityPlayer player;

    public final GuiTabManagerMain tabManagerMain;
    public final GuiTabManagerDatabase tabManagerDatabase;
    public final GuiLootTabTableEditor tabLootTableEditor;
    public final GuiLootTabPoolEditor tabLootPoolEditor;

    public GuiManager(EntityPlayer player) {
        super(new ContainerManager(player), false);
        this.player = player;

        tabManagerMain = new GuiTabManagerMain(0, this);
        tabManagerDatabase = new GuiTabManagerDatabase(1, this);
        tabLootTableEditor = new GuiLootTabTableEditor(2, this);
        tabLootPoolEditor = new GuiLootTabPoolEditor(3, this);

        tabList.add(tabManagerMain);
        tabList.add(tabManagerDatabase);
        tabList.add(tabLootTableEditor);
        tabList.add(tabLootPoolEditor);

        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.status.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.database.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));

        // tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.auction.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));
        // tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.banks.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));
        // tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.bank_accounts.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));
        // tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.currency.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.loot_tables.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.loot_pools.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));
        // tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.mail.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));
        // tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.shop.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));

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
        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        GL11.glPopMatrix();
    }
}
