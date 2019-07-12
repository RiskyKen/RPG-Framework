package moe.plushie.rpgeconomy.loot.client.gui;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiLabeledTextField;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiList;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiList.GuiListItem;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiScrollbar;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync.SyncType;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabLootPoolEditor extends GuiTabPanel<GuiTabbed> {

    private GuiLabeledTextField textSearchCategories;
    private GuiLabeledTextField textSearchNames;

    private GuiList listCategories;
    private GuiList listNames;

    private GuiScrollbar scrollCategories;
    private GuiScrollbar scrollNames;

    private GuiIconButton buttonAdd;
    private GuiIconButton buttonRemove;
    private GuiIconButton buttonEdit;

    public GuiTabLootPoolEditor(int tabId, GuiTabbed parent) {
        super(tabId, parent, false);
        textSearchCategories = new GuiLabeledTextField(fontRenderer, x + 5, y + 15, 100, 14);
        textSearchNames = new GuiLabeledTextField(fontRenderer, x + 110, y + 15, 100, 14);
        
        textSearchCategories.setEmptyLabel("Search categories");
        textSearchNames.setEmptyLabel("Search pools");
    }
    
    @Override
    public void tabChanged(int tabIndex) {
        if (tabIndex == getTabId()) {
            requestFromServer();
        }
        
    }

    private void requestFromServer() {
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientRequestSync(SyncType.LOOT_POOLS));
    }

    public void onGotFromServer(ArrayList<IIdentifier> identifiers, ArrayList<String> names, ArrayList<String> categories) {
        listCategories.clearList();
        listNames.clearList();
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
        textSearchCategories.y = y + 15;
        textSearchNames.x = x + 120;
        textSearchNames.y = y + 15;
        
        listCategories = new GuiList(x + 5, y + 35, 100, height - 40, 12);
        listNames = new GuiList(x + 120, y + 35, 100, height - 40, 12);
        
        scrollCategories = new GuiScrollbar(-1, x + 100 + 5, y + 35, 10, height - 40, "", false);
        scrollNames = new GuiScrollbar(-1, x + 210 + 10, y + 35, 10, height - 40, "", false);
        
        buttonAdd = new GuiIconButton(parent, 0, x + width - 20, y + 20, 16, 16, TEXTURE_BUTTONS);
        buttonAdd.setDrawButtonBackground(false).setIconLocation(208, 176, 16, 16);
        
        buttonRemove = new GuiIconButton(parent, -1, x + width - 20, y + 40, 16, 16, TEXTURE_BUTTONS);
        buttonRemove.setDrawButtonBackground(false).setIconLocation(208, 160, 16, 16);
        
        buttonEdit = new GuiIconButton(parent, -1, x + width - 20, y + 60, 16, 16, TEXTURE_BUTTONS);
        buttonEdit.setDrawButtonBackground(false).setIconLocation(208, 144, 16, 16);
        
        buttonList.add(scrollCategories);
        buttonList.add(scrollNames);
        buttonList.add(buttonAdd);
        buttonList.add(buttonRemove);
        buttonList.add(buttonEdit);
        //requestFromServer();
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
        boolean clicked = false;
        if (textSearchCategories.mouseClicked(mouseX, mouseY, button)) {
            clicked = true;
        }
        if (textSearchNames.mouseClicked(mouseX, mouseY, button)) {
            clicked = true;
        }
        if (listCategories.mouseClicked(mouseX, mouseY, button)) {
            clicked = true;
        }
        if (listNames.mouseClicked(mouseX, mouseY, button)) {
            clicked = true;
        }
        if (textSearchCategories.isFocused() & button == 1) {
            textSearchCategories.setText("");
        }
        if (textSearchNames.isFocused() & button == 1) {
            textSearchNames.setText("");
        }
        if (clicked) {
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
        for (int i = 0; i < buttonList.size(); i++) {
            buttonList.get(i).drawButton(mc, mouseX, mouseY, partialTickTime);
        }
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {

        //super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
    }
}
