package moe.plushie.rpgeconomy.loot.client.gui;

import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.loot.common.inventory.ContainerLootEditor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLootEditor extends GuiTabbed {
    
    private static int activeTab = 0;
    
    public GuiLootEditor(EntityPlayer player) {
        super(new ContainerLootEditor(player), false);
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
        activeTab = value;
    }

    @Override
    public String getName() {
        return "loot_editor";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_BACKGROUND);

        // Render editor background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize(), 64, 64, 4, zLevel);
    }
}
