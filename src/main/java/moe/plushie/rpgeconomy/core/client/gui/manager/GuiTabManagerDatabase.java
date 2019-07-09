package moe.plushie.rpgeconomy.core.client.gui.manager;

import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabManagerDatabase extends GuiTabPanel<GuiTabbed> {

    public GuiTabManagerDatabase(int tabId, GuiTabbed parent) {
        super(tabId, parent, false);
    }
    
    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_BACKGROUND);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height, 64, 64, 5, zLevel);
    }
}
