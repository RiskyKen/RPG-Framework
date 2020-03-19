package moe.plushie.rpg_framework.stats.client.gui;

import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.ModGuiContainer;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.stats.ModuleStats;
import moe.plushie.rpg_framework.stats.common.StatsWorld;
import moe.plushie.rpg_framework.stats.common.inventory.ContainerStats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStats extends ModGuiContainer<ContainerStats> {

    public GuiStats(EntityPlayer entityPlayer) {
        super(new ContainerStats(entityPlayer));
        this.xSize = 320;
        this.ySize = 240;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GuiHelper.renderBackgroundTexture(guiLeft, guiTop, xSize, ySize, zLevel);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(fontRenderer, xSize, getName());

        fontRenderer.drawString("Database Queue Size: " + DatabaseManager.getQueueSize(), 8, 20 + 11 * 0, 0x000000);
        fontRenderer.drawString("Server Time: " + ModuleStats.getServerStatsHandler().TIMER_SERVER.getAverageShort() + "ms", 8, 20 + 11 * 1, 0x000000);

        int[] historyServer = ModuleStats.getServerStatsHandler().TIMER_SERVER.getFullHistory();
        drawChart(8, ySize - 8, 1, historyServer, xSize - 16, 0x88DD0000, true);

        World world = Minecraft.getMinecraft().world;
        if (world != null) {
            StatsWorld statsWorld = ModuleStats.getWorldStatsHandler().getWorldStats(world.provider.getDimension());
            drawWorldStats(statsWorld);
        }
    }

    private void drawWorldStats(StatsWorld statsWorld) {
        if (statsWorld != null) {
            fontRenderer.drawString(String.format("World Time (%d): ", statsWorld.getDimensionID()) + statsWorld.getHistoryTickTime().getAverageShort() + "ms", 8, 20 + 11 * 2, 0x000000);
            fontRenderer.drawString("  Player Count: " + statsWorld.getPlayersCount() , 8, 20 + 11 * 3, 0x000000);
            fontRenderer.drawString("  Entity Count: " + statsWorld.getEntityCount() , 8, 20 + 11 * 4, 0x000000);
            fontRenderer.drawString("  Tile Count: " + statsWorld.getTileCount() , 8, 20 + 11 * 5, 0x000000);
            fontRenderer.drawString("  Ticking Tile Count: " + statsWorld.getTickingTileCount() , 8, 20 + 11 * 6, 0x000000);
            int[] historyWorld = statsWorld.getHistoryTickTime().getFullHistory();
            drawChart(8, ySize - 8, 1, historyWorld, xSize - 16, 0x8800DD00, true);
        }
    }

    private void drawChart(int left, int bot, int barWidth, int[] values, int widthCap, int colour, boolean fullHeight) {
        int barsWidth = MathHelper.clamp(values.length * barWidth, 1, widthCap);
        for (int i = 0; i < 6; i++) {
            drawRect(left, bot - 1 - 10 * i, left + barsWidth, bot - 10 * i, 0xFF000000);
        }
        for (int i = 0; i < 5; i++) {
            drawRect(left, bot - 1 - 5 - 10 * i, left + barsWidth, bot - 5 - 10 * i, 0x44000000);
        }
        int lastValue = values[values.length - 1];
        for (int i = 0; i < values.length; i++) {
            if (i >= barsWidth) {
                break;
            }
            int value = values[values.length - i - 1];
            if (fullHeight) {
                drawRect(left + i * barWidth, bot - value, left + barWidth * i + barWidth, bot, colour);
            } else {
                drawRect(left + i * barWidth, bot - value, left + barWidth * i + barWidth, bot - lastValue + 1, colour);
            }
            lastValue = value;
        }
    }
}
