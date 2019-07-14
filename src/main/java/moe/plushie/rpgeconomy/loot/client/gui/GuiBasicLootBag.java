package moe.plushie.rpgeconomy.loot.client.gui;

import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.core.common.lib.LibItemNames;
import moe.plushie.rpgeconomy.loot.common.inventory.ContainerBasicLootBag;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBasicLootBag extends GuiContainer {

    protected static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(LibGuiResources.BACKGROUND);
    protected static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.BUTTONS);
    protected static final ResourceLocation TEXTURE_ICONS = new ResourceLocation(LibGuiResources.ICONS);
    
    public GuiBasicLootBag(EntityPlayer player, ItemStack stack) {
        super(new ContainerBasicLootBag(player, stack));
    }
    
    @Override
    public void initGui() {
        this.xSize = 300;
        this.ySize = 240;
        super.initGui();
        buttonList.clear();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_BACKGROUND);
        // Main background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize() - 99, 64, 64, 5, zLevel);
        
        // Title box.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 5, getGuiTop() + 4, 0, 64, getXSize() - 10, 14, 64, 13, 2, zLevel);
        
        // Slots Box.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 5, getGuiTop() + 20, 0, 64, 114, 115, 64, 13, 2, zLevel);
        
        // Other Box.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 121, getGuiTop() + 20, 0, 64, 174, 115, 64, 13, 2, zLevel);
        
        // Player inv background.
        GuiHelper.renderPlayerInvTexture(getGuiLeft(), getGuiTop() + getYSize() - 98);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(fontRenderer, xSize, LibItemNames.BASIC_LOOT_BAG);
        
        GuiHelper.renderPlayerInvlabel(0, getYSize() - 98, fontRenderer);
    }
}
