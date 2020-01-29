package moe.plushie.rpg_framework.mail.client.gui;

import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.mail.client.MailCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMailIcon extends GuiScreen {

    private final static ResourceLocation ICONS = new ResourceLocation(LibGuiResources.ICONS);

    public GuiMailIcon() {
        MinecraftForge.EVENT_BUS.register(this);
        mc = Minecraft.getMinecraft();
        fontRenderer = mc.fontRenderer;
        width = mc.displayWidth;
        height = mc.displayHeight;
    }

    @SubscribeEvent
    public void onRenderTickEvent(RenderTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        width = scaledResolution.getScaledWidth();
        height = scaledResolution.getScaledHeight();
        for (IMailSystem mailSystem : RPGFramework.getProxy().getMailSystemManager().getMailSystems()) {
            int messageCount = MailCounter.getUnreadMailCount(mailSystem);
            if (messageCount < 1) {
                return;
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableBlend();
            GlStateManager.resetColor();
            GlStateManager.color(1F, 1F, 1F, 0.75F);
            mc.renderEngine.bindTexture(ICONS);
            int iconIndex = 19;
            int x = width - 16 - 1;
            int y = 1;

            int iconY = MathHelper.floor(iconIndex / 16);
            int iconX = iconIndex - (x * 16);

            drawTexturedModalRect(x, y, 16 * iconX, 16 * iconY, 16, 16);
            fontRenderer.drawStringWithShadow(String.valueOf(messageCount), x + 2, y + 6, 0xFFFFFF);
        }
    }
}
