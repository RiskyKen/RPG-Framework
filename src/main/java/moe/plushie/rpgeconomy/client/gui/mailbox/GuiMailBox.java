package moe.plushie.rpgeconomy.client.gui.mailbox;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpgeconomy.client.controls.GuiTab;
import moe.plushie.rpgeconomy.client.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.client.controls.GuiTabbed;
import moe.plushie.rpgeconomy.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.common.init.ModBlocks;
import moe.plushie.rpgeconomy.common.inventory.ContainerMailBox;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMailBox extends GuiTabbed {
    
    private static final ResourceLocation TEXTURE_TABS = new ResourceLocation(LibGuiResources.MAIL_BOX_TABS);
    
    private final String inventoryName;
    
    public final GuiTabMailBoxReading tabReading;
    public final GuiTabMailBoxSending tabSending;
    
    public GuiMailBox(InventoryPlayer inventoryPlayer) {
        super(new ContainerMailBox(inventoryPlayer), false, TEXTURE_TABS);
        
        this.inventoryName = ModBlocks.MAIL_BOX.getTranslationKey();
        
        tabReading = new GuiTabMailBoxReading(0, this);
        tabSending = new GuiTabMailBoxSending(1, this);
        
        tabList.add(tabReading);
        tabList.add(tabSending);
        
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.reading")).setIconLocation(52, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(1, 150));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.sending")).setIconLocation(52 + 16, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(1, 150));
        
        tabController.setActiveTabIndex(getActiveTab());
        tabChanged();
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
