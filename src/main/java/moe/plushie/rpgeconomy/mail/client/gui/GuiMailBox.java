package moe.plushie.rpgeconomy.mail.client.gui;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTab;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.core.common.lib.LibBlockNames;
import moe.plushie.rpgeconomy.mail.common.inventory.ContainerMailBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMailBox extends GuiTabbed {

    private static final ResourceLocation TEXTURE_TABS = new ResourceLocation(LibGuiResources.MAIL_BOX_TABS);

    public final GuiTabMailBoxReading tabReading;
    public final GuiTabMailBoxSending tabSending;

    public GuiMailBox(EntityPlayer entityPlayer) {
        super(new ContainerMailBox(entityPlayer), false, TEXTURE_TABS);
        tabReading = new GuiTabMailBoxReading(0, this);
        tabSending = new GuiTabMailBoxSending(1, this);

        tabList.add(tabReading);
        tabList.add(tabSending);

        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(getName(), "tab.reading.name")).setIconLocation(52, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(1, 150));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(getName(), "tab.sending.name")).setIconLocation(52 + 16, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(1, 150));

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
}
