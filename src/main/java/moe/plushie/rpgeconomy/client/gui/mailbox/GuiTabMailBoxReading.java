package moe.plushie.rpgeconomy.client.gui.mailbox;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moe.plushie.rpgeconomy.client.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.client.lib.LibGuiResources;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

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
    public void drawForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(fontRenderer, width, "Reading");
        super.drawForegroundLayer(mouseX, mouseY);
    }
}
