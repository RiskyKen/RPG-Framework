package moe.plushie.rpg_framework.mail.client.gui;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabMailBoxReading extends GuiTabPanel<GuiMailBox> {
    
    private static final ResourceLocation TEXTURE_READING = new ResourceLocation(LibGuiResources.MAIL_BOX_READING);
    
    public GuiTabMailBoxReading(int tabId, GuiMailBox parent) {
        super(tabId, parent, false);
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(TEXTURE_READING);
        drawTexturedModalRect(x, y, 0, 0, width, height);
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        
        GuiHelper.renderLocalizedGuiName(fontRenderer, parent.getXSize(), parent.getName() + ".tab.reading");
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
    }
}
