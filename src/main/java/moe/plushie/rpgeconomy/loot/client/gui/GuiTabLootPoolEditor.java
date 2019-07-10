package moe.plushie.rpgeconomy.loot.client.gui;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiList;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiList.GuiListItem;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiScrollbar;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync.SyncType;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabLootPoolEditor extends GuiTabPanel<GuiTabbed> {

    private GuiTextField textSearchCategories;
    private GuiTextField textSearchNames;

    private GuiList listCategories;
    private GuiList listNames;

    private GuiScrollbar scrollCategories;
    private GuiScrollbar scrollNames;

    private GuiIconButton buttonAdd;
    private GuiIconButton buttonRemove;

    public GuiTabLootPoolEditor(int tabId, GuiTabbed parent) {
        super(tabId, parent, false);
        textSearchCategories = new GuiTextField(-1, fontRenderer, x + 5, y + 5, 100, 14);
        textSearchNames = new GuiTextField(-1, fontRenderer, x + 110, y + 5, 100, 14);
    }
    
    @Override
    public void tabChanged(int tabIndex) {
        requestFromServer();
    }

    private void requestFromServer() {
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientRequestSync(SyncType.LOOT_POOLS));
    }

    public void onGotFromServer(ArrayList<IIdentifier> identifiers, ArrayList<String> names, ArrayList<String> categories) {
        RpgEconomy.getLogger().info("Got lists " + identifiers.size());
        for (String category : categories) {
            if (!listCategories.contains(category)) {
                listCategories.addListItem(new GuiListItem(category));
            }
        }
        for (int i = 0; i < identifiers.size(); i++) {
            listNames.addListItem(new GuiListItem(names.get(i), String.valueOf(identifiers.get(i).getValue())));
        }
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        textSearchCategories.x = x + 5;
        textSearchNames.x = x + 110;
        
        listCategories = new GuiList(x + 5, y + 25, 100, height - 30, 12);
        listNames = new GuiList(x + 110, y + 25, 100, height - 30, 12);
        requestFromServer();
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textSearchCategories.textboxKeyTyped(c, keycode)) {
            return true;
        }
        if (textSearchNames.textboxKeyTyped(c, keycode)) {
            return true;
        }
        return super.keyTyped(c, keycode);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (textSearchCategories.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (textSearchNames.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (listCategories.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (listNames.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_BACKGROUND);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height, 64, 64, 5, zLevel);
        listCategories.drawList(mouseX, mouseY, partialTickTime);
        listNames.drawList(mouseX, mouseY, partialTickTime);
        textSearchCategories.drawTextBox();
        textSearchNames.drawTextBox();
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {

        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
    }
}
