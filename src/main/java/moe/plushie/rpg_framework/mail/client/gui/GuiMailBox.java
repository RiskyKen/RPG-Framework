package moe.plushie.rpg_framework.mail.client.gui;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTab;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpg_framework.core.common.lib.LibBlockNames;
import moe.plushie.rpg_framework.mail.common.inventory.ContainerMailBox;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMailBox extends GuiTabbed {

    private final TileEntityMailBox tileEntity;
    private final GuiTabMailBoxReading tabReading;
    private final GuiTabMailBoxSending tabSending;
    private static int activeTabIndex = 0;

    public GuiMailBox(TileEntityMailBox tileEntity, EntityPlayer entityPlayer) {
        super(new ContainerMailBox(tileEntity, entityPlayer), false);
        this.tileEntity = tileEntity;

        tabReading = new GuiTabMailBoxReading(0, this);
        tabSending = new GuiTabMailBoxSending(1, this);

        tabList.add(tabReading);
        tabList.add(tabSending);

        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.reading.name")).setIconLocation(16, 16).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(1, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.sending.name")).setIconLocation(32, 16).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(1, 150));

        tabController.setActiveTabIndex(getActiveTab());
        tabChanged();
    }

    @Override
    public String getName() {
        return LibBlockNames.MAIL_BOX;
    }

    @Override
    public void initGui() {
        this.xSize = 176;
        this.ySize = 224;
        super.initGui();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                tab.drawBackgroundLayer(partialTickTime, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                tab.drawForegroundLayer(mouseX, mouseY, 0);
            }
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        GL11.glPopMatrix();
    }

    public TileEntityMailBox getTileEntity() {
        return tileEntity;
    }

    @Override
    protected int getActiveTab() {
        return activeTabIndex;
    }

    @Override
    protected void setActiveTab(int value) {
        activeTabIndex = value;
    }
}