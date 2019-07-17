package moe.plushie.rpg_framework.loot.client.gui;

import java.util.ArrayList;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientRequestSync;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientRequestSync.SyncType;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLootTabTableEditor extends GuiTabPanel<GuiTabbed> {

    public GuiLootTabTableEditor(int tabId, GuiTabbed parent) {
        super(tabId, parent, false);
        requestFromServer();
    }

    private void requestFromServer() {
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientRequestSync(SyncType.LOOT_TABLES));
    }

    public void onGotFromServer(ArrayList<IIdentifier> identifiers, ArrayList<String> names, ArrayList<String> categories) {

    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_BACKGROUND);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height, 64, 64, 5, zLevel);
    }
}
