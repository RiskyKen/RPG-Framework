package moe.plushie.rpg_framework.loot.client.gui;

import java.util.ArrayList;

import moe.plushie.rpg_framework.api.loot.ILootTable;
import moe.plushie.rpg_framework.api.loot.ILootTablePool;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTab;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.loot.common.inventory.ContainerLootEditor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLootEditor extends GuiTabbed {

    private static int activeTab = 0;

    public final GuiLootTabTableEditor tabLootTableEditor;
    public final GuiLootTabPoolEditor tabLootPoolEditor;

    public GuiLootEditor(EntityPlayer player) {
        super(new ContainerLootEditor(player), false);

        tabLootTableEditor = new GuiLootTabTableEditor(0, this);
        tabLootPoolEditor = new GuiLootTabPoolEditor(1, this);

        tabList.add(tabLootTableEditor);
        tabList.add(tabLootPoolEditor);

        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.loot_tables.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.loot_pools.name")).setIconLocation(64, 16).setPadding(0, 4, 3, 3));

        tabController.setActiveTabIndex(getActiveTab());
        tabController.setTabsPerSide(9);

        tabChanged();
        
        ArrayList<Slot> playerSlots = ((ContainerLootEditor)inventorySlots).getSlotsPlayer();
        for (Slot slot : playerSlots) {
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setVisible(false);
            }
        }
    }

    @Override
    public void initGui() {
        this.xSize = 320;
        this.ySize = 240;
        super.initGui();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        
        GlStateManager.color(1F, 1F, 1F, 1F);

        GlStateManager.disableDepth();
        //GlStateManager.enableBlend();
        
        for (GuiTabPanel tabPanel : tabList) {
            if (tabPanel.getTabId() == activeTab) {
                GlStateManager.pushAttrib();
                tabPanel.drawBackgroundLayer(partialTicks, mouseX, mouseY);
                GlStateManager.popAttrib();
            }
        }
        
        GlStateManager.pushAttrib();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.popAttrib();
        
        GlStateManager.pushAttrib();
        //GlStateManager.enableBlend();
        //GlStateManager.enableAlpha();
        
        if (!tabList.get(activeTab).isDialogOpen()) {
            tabController.drawHoverText(mc, mouseX, mouseY);
            renderHoveredToolTip(mouseX, mouseY);
        }

        GlStateManager.enableBlend();
        GlStateManager.popAttrib();
        
        for (GuiTabPanel tabPanel : tabList) {
            if (tabPanel.getTabId() == activeTab) {
                GlStateManager.pushAttrib();
                tabPanel.drawForegroundLayer(mouseX, mouseY, partialTicks);
                GlStateManager.popAttrib();
            }
        }
        

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
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }
    
    public void gotTableFromServer(ILootTable table) {
        
    }
    
    public void gotPoolFromServer(ILootTablePool pool) {
        tabLootPoolEditor.gotPoolFromServer(pool);
    }
}
