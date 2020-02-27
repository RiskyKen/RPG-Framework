package moe.plushie.rpg_framework.stats.client.gui;

import moe.plushie.rpg_framework.core.client.gui.ModGuiContainer;
import moe.plushie.rpg_framework.stats.common.inventory.ContainerStats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStats extends ModGuiContainer<ContainerStats> {

    public GuiStats(ContainerStats container) {
        super(container);
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    }
}
