package moe.plushie.rpg_framework.loot.client.gui;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientLootEditorUpdate;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientLootEditorUpdate.LootEditType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiLootDialogRemove extends AbstractGuiDialog {

    private GuiButtonExt buttonCancel;
    private GuiButtonExt buttonOK;
    
    private final IIdentifier poolIdentifier;
    private final String poolName;

    public GuiLootDialogRemove(GuiScreen parent, String name, IDialogCallback callback, IIdentifier poolIdentifier, String poolName) {
        super(parent, name, callback, 190, 100);
        this.poolIdentifier = poolIdentifier;
        this.poolName = poolName;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonCancel = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Cancel");
        buttonOK = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "Remove");

        buttonList.add(buttonCancel);
        buttonList.add(buttonOK);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonCancel) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonOK) {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientLootEditorUpdate(LootEditType.LOOT_POOL_REMOVE).setRemoveData(poolIdentifier));
            returnDialogResult(DialogResult.OK);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        drawTitle();
    }
}