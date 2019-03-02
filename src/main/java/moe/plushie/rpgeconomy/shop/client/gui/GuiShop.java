package moe.plushie.rpgeconomy.shop.client.gui;

import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.init.ModBlocks;
import moe.plushie.rpgeconomy.core.common.inventory.slot.SlotCurrency;
import moe.plushie.rpgeconomy.shop.common.inventory.ContainerShop;
import moe.plushie.rpgeconomy.shop.common.tileentities.TileEntityShop;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiShop extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.SHOP);
    private static final int TEXTURE_SIZE_X = 176;
    private static final int TEXTURE_SIZE_Y = 137;
    
    public GuiShop(EntityPlayer entityPlayer, TileEntityShop tileEntity) {
        super(new ContainerShop(entityPlayer, tileEntity));
    }
    
    @Override
    public void initGui() {
        this.xSize = TEXTURE_SIZE_X;
        this.ySize = TEXTURE_SIZE_Y;
        if (ConfigHandler.showPlayerInventoryInWalletGUI) {
            this.ySize += 98 + 1;
        }
        super.initGui();
        buttonList.clear();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, TEXTURE_SIZE_X, TEXTURE_SIZE_Y);
        for (Slot slot : inventorySlots.inventorySlots) {
            if (slot instanceof SlotCurrency) {
                //drawTexturedModalRect(getGuiLeft() + slot.xPos - 1, getGuiTop() + slot.yPos - 1, 238, 0, 18, 18);
            }
        }
        if (ConfigHandler.showPlayerInventoryInWalletGUI) {
            GuiHelper.renderPlayerInvTexture(getGuiLeft(), getGuiTop() + TEXTURE_SIZE_Y + 1);
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        ItemStack itemStack = new ItemStack(ModBlocks.SHOP);
        String title = itemStack.getDisplayName();
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, xSize / 2 - titleWidth / 2, 6, 0x333333);
        
        if (ConfigHandler.showPlayerInventoryInWalletGUI) {
            GuiHelper.renderPlayerInvlabel(0, TEXTURE_SIZE_Y + 1, fontRenderer);
        }
        
        fontRenderer.drawString("Stock: 50", 25, 20, 0x333333, false);
        fontRenderer.drawString("Cost: 10,000", 25, 30, 0x333333, false);
        
        fontRenderer.drawString("Stock: 4", 25, 43, 0x333333, false);
        fontRenderer.drawString("Cost: 10,000,000", 25, 53, 0x333333, false);
        
        fontRenderer.drawString("Stock: 50,000", 25, 66, 0x333333, false);
        fontRenderer.drawString("Cost: 5", 25, 76, 0x333333, false);
        
        fontRenderer.drawString("Stock: 5000", 25, 89, 0x333333, false);
        fontRenderer.drawString("Cost: 100,000", 25, 99, 0x333333, false);
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -guiTop, 0);
        for (GuiButton button : buttonList) {
            if (button instanceof GuiIconButton) {
                //((GuiIconButton)button).drawRollover(mc, mouseX, mouseY);
            }
        }
        GlStateManager.popMatrix();
    }
}
