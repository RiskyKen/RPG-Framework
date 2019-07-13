package moe.plushie.rpgeconomy.loot.client.gui;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiLabeledTextField;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientLootEditorUpdate;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientLootEditorUpdate.LootEditType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiLootDialogRename extends AbstractGuiDialog {

    private GuiButtonExt buttonCancel;
    private GuiButtonExt buttonOK;
    private GuiLabeledTextField textName;
    private GuiLabeledTextField textCategory;
    
    private final IIdentifier poolIdentifier;

    public GuiLootDialogRename(GuiScreen parent, String name, IDialogCallback callback, IIdentifier poolIdentifier, String poolName, String poolCategory) {
        super(parent, name, callback, 190, 100);
        this.poolIdentifier = poolIdentifier;
        textName = new GuiLabeledTextField(fontRenderer, 0, 0, width - 20, 12);
        textCategory = new GuiLabeledTextField(fontRenderer, 0, 0, width - 20, 12);
        
        textName.setText(poolName);
        textCategory.setText(poolCategory);
        
        textName.setEmptyLabel("Enter name");
        textCategory.setEmptyLabel("Enter category");
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonCancel = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Cancel");
        buttonOK = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "Rename");

        textName.x = x + 10;
        textName.y = y + 18;

        textCategory.x = x + 10;
        textCategory.y = y + 38;
        
        buttonList.add(buttonCancel);
        buttonList.add(buttonOK);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonCancel) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonOK) {
            String poolName = textName.getText();
            String poolCategory = textCategory.getText();;
            if (!StringUtils.isNullOrEmpty(poolName) & !StringUtils.isNullOrEmpty(poolCategory)) {
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientLootEditorUpdate(LootEditType.LOOT_POOL_RENAME).setRenameData(poolIdentifier, poolName, poolCategory));
                returnDialogResult(DialogResult.OK);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        textName.mouseClicked(mouseX, mouseY, button);
        textCategory.mouseClicked(mouseX, mouseY, button);
        if (button == 1 & textName.isFocused()) {
            textName.setText("");
        }
        if (button == 1 & textCategory.isFocused()) {
            textCategory.setText("");
        }
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textName.textboxKeyTyped(c, keycode)) {
            return true;
        }
        if (textCategory.textboxKeyTyped(c, keycode)) {
            return true;
        }
        return super.keyTyped(c, keycode);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        drawTitle();
        textName.drawTextBox();
        textCategory.drawTextBox();
    }
}
