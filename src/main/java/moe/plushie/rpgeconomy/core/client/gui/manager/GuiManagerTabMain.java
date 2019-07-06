package moe.plushie.rpgeconomy.core.client.gui.manager;

import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiManagerTabMain extends GuiTabPanel<GuiTabbed> {

    private static final ResourceLocation TEXTURE_MANAGER = new ResourceLocation(LibGuiResources.MANAGER);
    
    public GuiManagerTabMain(int tabId, GuiTabbed parent) {
        super(tabId, parent, false);
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_MANAGER);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height, 100, 100, 5, zLevel);
    }
}
