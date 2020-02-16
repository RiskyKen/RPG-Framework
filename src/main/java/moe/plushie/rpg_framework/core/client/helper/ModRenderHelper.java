package moe.plushie.rpg_framework.core.client.helper;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ModRenderHelper {

    private static float lightX;
    private static float lightY;

    private ModRenderHelper() {
    }

    public static void disableLighting() {
        lightX = OpenGlHelper.lastBrightnessX;
        lightY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
    }

    public static void enableLighting() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
    }

    public static void setLightingForBlock(World world, BlockPos pos) {
        int i = world.getCombinedLight(pos, 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
    }

    public static void enableAlphaBlend() {
        enableAlphaBlend(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    public static void enableAlphaBlend(SourceFactor sourceFactor, DestFactor destFactor) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(sourceFactor.factor, destFactor.factor);
    }

    public static void disableAlphaBlend() {
        GlStateManager.disableBlend();
    }

    public static void enableScissorScaled(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        double scaledWidth = mc.displayWidth / sr.getScaledWidth_double();
        double scaledHeight = mc.displayHeight / sr.getScaledHeight_double();
        enableScissor(
                MathHelper.floor(x * scaledWidth),
                mc.displayHeight - MathHelper.floor(((double)y + (double)height) * scaledHeight),
                MathHelper.floor(width * scaledWidth),
                MathHelper.floor(height * scaledHeight));
    }

    public static void enableScissor(int x, int y, int width, int height) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x, y, width, height);
    }

    public static void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
