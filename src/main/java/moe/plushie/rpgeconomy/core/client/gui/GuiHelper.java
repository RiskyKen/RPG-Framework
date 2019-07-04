package moe.plushie.rpgeconomy.core.client.gui;

import java.util.Iterator;
import java.util.List;

import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiHelper {
    
    private static final ResourceLocation PLAYER_TEXTURE = new ResourceLocation(LibGuiResources.PLAYER_INVENTORY);
    
    private GuiHelper() {}
    
    public static void renderPlayerInvTexture(int x, int y) {
        Minecraft.getMinecraft().renderEngine.bindTexture(PLAYER_TEXTURE);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 176, 98, 256, 256);
    }
    
    public static void renderPlayerInvlabel(int x, int y, FontRenderer fontRenderer) {
        renderPlayerInvlabel(x, y, fontRenderer, 0x333333);
    }
    
    public static void renderPlayerInvlabel(int x, int y, FontRenderer fontRenderer, int colour) {
        fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), x + 8, y + 5, colour);
    }
    
    public static void renderLocalizedGuiName(FontRenderer fontRenderer, int xSize, String name) {
        renderLocalizedGuiName(fontRenderer, xSize, name, null, 4210752);
    }
    
    public static void renderLocalizedGuiName(FontRenderer fontRenderer, int xSize, String name, int colour) {
        renderLocalizedGuiName(fontRenderer, xSize, name, null, colour);
    }
    
    public static void renderLocalizedGuiName(FontRenderer fontRenderer, int xSize, String name, String append) {
        renderLocalizedGuiName(fontRenderer, xSize, name, append, 4210752);
    }
    
    public static void renderLocalizedGuiName(FontRenderer fontRenderer, int xSize, String name, String append, int colour) {
        String unlocalizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + name + ".name";
        String localizedName = I18n.format(unlocalizedName);
        String renderText = unlocalizedName;
        if (!unlocalizedName.equals(localizedName)){
            renderText = localizedName;
        }
        if (append != null) {
            renderText = renderText + " - " + append;
        }
        int xPos = xSize / 2 - fontRenderer.getStringWidth(renderText) / 2;
        fontRenderer.drawString(renderText, xPos, 6, colour);
    }
    
    public static String getLocalControlName(String guiName, String controlName) {
        return I18n.format("inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + "." + controlName);
    }
    
    public static void drawHoveringText(List textList, int xPos, int yPos, FontRenderer font, int width, int height, float zLevel) {
        if (!textList.isEmpty()) {
            GlStateManager.pushAttrib();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int textWidth = 0;
            Iterator iterator = textList.iterator();

            while (iterator.hasNext()) {
                String line = (String)iterator.next();
                int sWidth = font.getStringWidth(line);
                if (sWidth > textWidth) {
                    textWidth = sWidth;
                }
            }

            int renderX = xPos + 12;
            int renderY = yPos - 12;
            int textHeight = 8;

            if (textList.size() > 1) {
                textHeight += 2 + (textList.size() - 1) * 10;
            }

            if (renderX + textWidth > width - 2) {
                renderX -= 28 + textWidth;
            }

            if (renderY + textHeight + 6 > height) {
                renderY = height - textHeight - 6;
            }
            
            if (renderY < 5) {
                renderY = 5;
            }

            zLevel = 300.0F;
            int j1 = -267386864;
            drawGradientRect(renderX - 3, renderY - 4, renderX + textWidth + 3, renderY - 3, j1, j1, zLevel);
            drawGradientRect(renderX - 3, renderY + textHeight + 3, renderX + textWidth + 3, renderY + textHeight + 4, j1, j1, zLevel);
            drawGradientRect(renderX - 3, renderY - 3, renderX + textWidth + 3, renderY + textHeight + 3, j1, j1, zLevel);
            drawGradientRect(renderX - 4, renderY - 3, renderX - 3, renderY + textHeight + 3, j1, j1, zLevel);
            drawGradientRect(renderX + textWidth + 3, renderY - 3, renderX + textWidth + 4, renderY + textHeight + 3, j1, j1, zLevel);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            drawGradientRect(renderX - 3, renderY - 3 + 1, renderX - 3 + 1, renderY + textHeight + 3 - 1, k1, l1, zLevel);
            drawGradientRect(renderX + textWidth + 2, renderY - 3 + 1, renderX + textWidth + 3, renderY + textHeight + 3 - 1, k1, l1, zLevel);
            drawGradientRect(renderX - 3, renderY - 3, renderX + textWidth + 3, renderY - 3 + 1, k1, k1, zLevel);
            drawGradientRect(renderX - 3, renderY + textHeight + 2, renderX + textWidth + 3, renderY + textHeight + 3, l1, l1, zLevel);

            for (int i2 = 0; i2 < textList.size(); ++i2) {
                String line = (String)textList.get(i2);
                font.drawStringWithShadow(line, renderX, renderY, -1);
                if (i2 == 0) {
                    renderY += 2;
                }
                renderY += 10;
            }

            zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.popAttrib();
        }
    }
    
    private static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor, float zLevel) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        //GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        //GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
