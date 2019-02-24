package moe.plushie.rpgeconomy.mail.client.gui;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabMailBoxReading extends GuiTabPanel {
    
    private static final ResourceLocation TEXTURE_READING = new ResourceLocation(LibGuiResources.MAIL_BOX_READING);
    
    public GuiTabMailBoxReading(int tabId, GuiScreen parent) {
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
        GuiHelper.renderLocalizedGuiName(fontRenderer, width, "Reading");
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
    }
}
