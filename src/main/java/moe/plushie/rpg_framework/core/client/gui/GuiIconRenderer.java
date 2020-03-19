package moe.plushie.rpg_framework.core.client.gui;

import moe.plushie.rpg_framework.api.core.IGuiIcon;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.mail.client.MailCounter;
import moe.plushie.rpg_framework.stats.ModuleStats;
import moe.plushie.rpg_framework.stats.client.gui.GuiStats;
import moe.plushie.rpg_framework.stats.common.StatsWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIconRenderer extends GuiScreen {

    private final static ResourceLocation ICONS = new ResourceLocation(LibGuiResources.ICONS);

    public GuiIconRenderer() {
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

        if (RPGFramework.isDebugging() & !(mc.currentScreen instanceof GuiStats)) {

            World world = Minecraft.getMinecraft().world;
            if (world != null) {
                StatsWorld statsWorld = ModuleStats.getWorldStatsHandler().getWorldStats(world.provider.getDimension());
                fontRenderer.drawString("Database Queue Size: " + DatabaseManager.getQueueSize(), 1, 1, 0xFFFFFF);
                fontRenderer.drawString("Server Time: " + ModuleStats.getServerStatsHandler().TIMER_SERVER.getAverageShort() + "ms", 1, 11, 0xFFFFFF);
                if (statsWorld != null) {
                    fontRenderer.drawString(String.format("World Time (%d): ", statsWorld.getDimensionID()) + statsWorld.getHistoryTickTime().getAverageShort() + "ms", 1, 21, 0xFFFFFF);
                }
            }
        }

        for (IMailSystem mailSystem : RPGFramework.getProxy().getMailSystemManager().getMailSystems()) {
            int messageCount = MailCounter.getUnreadMailCount(mailSystem);
            if (messageCount < 1) {
                return;
            }

            for (IGuiIcon guiIcon : mailSystem.getGuiIcons()) {
                RenderHelper.disableStandardItemLighting();
                GlStateManager.enableBlend();
                GlStateManager.resetColor();
                GlStateManager.color(1F, 1F, 1F, guiIcon.getIconAlpha());
                mc.renderEngine.bindTexture(ICONS);

                boolean foundClass = false;
                if (mc.currentScreen != null) {
                    String curClass = mc.currentScreen.getClass().getName();
                    for (String classPath : guiIcon.getClassPaths()) {
                        if (classPath.equals(curClass)) {
                            foundClass = true;
                            break;
                        }
                    }
                } else {
                    for (String classPath : guiIcon.getClassPaths()) {
                        if (classPath.equals("")) {
                            foundClass = true;
                            break;
                        }
                    }
                }

                if (!foundClass) {
                    continue;
                }

                int iconIndex = guiIcon.getIconIndex();
                int x = 0;
                int y = 0;
                switch (guiIcon.getAnchorHorizontal()) {
                case LEFT:
                    x = 0;
                    break;
                case CENTER:
                    x = width / 2 - 8;
                    break;
                case RIGHT:
                    x = width - 16;
                    break;
                }
                switch (guiIcon.getAnchorVertical()) {
                case TOP:
                    y = 0;
                    break;
                case CENTER:
                    y = height / 2 - 8;
                    break;
                case BOTTOM:
                    y = height - 16;
                    break;
                }
                x += guiIcon.getOffsetHorizontal();
                y += guiIcon.getOffsetVertical();

                int iconY = MathHelper.floor(iconIndex / 16);
                int iconX = iconIndex - (x * 16);
                drawTexturedModalRect(x, y, 16 * iconX, 16 * iconY, 16, 16);
                // fontRenderer.drawStringWithShadow(String.valueOf(messageCount), x + 2, y + 6, 0xFFFFFF);
            }
        }
    }
}
