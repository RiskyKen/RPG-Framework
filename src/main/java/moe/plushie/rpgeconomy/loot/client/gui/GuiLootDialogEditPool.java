package moe.plushie.rpgeconomy.loot.client.gui;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.api.loot.ILootTablePool;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientLootEditorUpdate;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientLootEditorUpdate.LootEditType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiLootDialogEditPool extends AbstractGuiDialog {

    private GuiButtonExt buttonCancel;
    private GuiButtonExt buttonOK;
    
    private ILootTablePool pool = null;
    
    public GuiLootDialogEditPool(GuiScreen parent, String name, IDialogCallback callback, IIdentifier poolIdentifier) {
        super(parent, name, callback, 280, 220);
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientLootEditorUpdate(LootEditType.LOOT_POOL_REQUEST).setRequestPoolData(poolIdentifier));
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonCancel = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30 - 98, 80, 20, "Cancel");
        buttonOK = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30 - 98, 80, 20, "Edit");
        
        buttonList.add(buttonCancel);
        buttonList.add(buttonOK);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonCancel) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonOK) {
            if (pool != null) {
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientLootEditorUpdate(LootEditType.LOOT_POOL_EDIT).setEditPoolData(pool));
                returnDialogResult(DialogResult.OK);
            }
        }
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        drawParentCoverBackground();
        int textureWidth = 176;
        int textureHeight = 62;
        int borderSize = 4;
        mc.renderEngine.bindTexture(TEXTURE);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height - 99, textureWidth, textureHeight, borderSize, zLevel);
        GuiHelper.renderPlayerInvTexture(x, y + height - 98);
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        if (pool == null) {
            drawTitle("LOADING - " + name + " - LOADING");
        } else {
            drawTitle();
        }
    }
    
}
