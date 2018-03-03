package moe.plushie.rpgeconomy.client.gui;

import moe.plushie.rpgeconomy.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.common.inventory.ContainerMailBox;
import moe.plushie.rpgeconomy.common.tileentities.TileEntityMailBox;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiMailBox extends GuiContainer {

    private static final ResourceLocation TEXTURE_READING = new ResourceLocation(LibGuiResources.MAIL_BOX_READING);
    private static final ResourceLocation TEXTURE_SENDING = new ResourceLocation(LibGuiResources.MAIL_BOX_SENDING);
    
    public GuiMailBox(InventoryPlayer inventoryPlayer, TileEntityMailBox tileEntity) {
        super(new ContainerMailBox(inventoryPlayer, tileEntity));
    }

    @Override
    public void initGui() {
        this.xSize = 176;
        this.ySize = 224;
        super.initGui();
        buttonList.clear();
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_READING);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        //drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xFFFFFFFF);
    }
}
