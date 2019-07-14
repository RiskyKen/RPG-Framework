package moe.plushie.rpgeconomy.loot.client.gui;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.api.loot.ILootTableItem;
import moe.plushie.rpgeconomy.api.loot.ILootTablePool;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.GuiSlotHandler;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientLootEditorUpdate;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientLootEditorUpdate.LootEditType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiLootDialogEditPool extends AbstractGuiDialog {
    
    private final GuiSlotHandler slotHandler;
    private GuiButtonExt buttonCancel;
    private GuiButtonExt buttonOK;
    
    private ILootTablePool pool = null;
    
    public GuiLootDialogEditPool(GuiScreen parent, String name, IDialogCallback callback, IIdentifier poolIdentifier) {
        super(parent, name, callback, 320, 240);
        this.slotHandler = new GuiSlotHandler((GuiContainer) parent);
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
        GlStateManager.disableLighting();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
        drawParentCoverBackground();
        
        int textureWidth = 176;
        int textureHeight = 62;
        int borderSize = 4;
        mc.renderEngine.bindTexture(TEXTURE);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height - 99, textureWidth, textureHeight, borderSize, zLevel);
        GuiHelper.renderPlayerInvTexture(x + width / 2 - 176 / 2, y + height - 98);
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        if (pool == null) {
            drawTitle("LOADING - " + name + " - LOADING");
            return;
        }
        
        drawTitle(name + " - " + pool.getName());
        RenderItem ri = mc.getRenderItem();
        
        GlStateManager.pushAttrib();
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        for (int i = 0; i < pool.getPoolItems().size(); i++) {
            ILootTableItem item = pool.getPoolItems().get(i);
            ri.renderItemAndEffectIntoGUI(item.getItem(), x + 8 + i * 18, y + 25);
            ri.renderItemOverlayIntoGUI(fontRenderer, item.getItem(), x + 8 + i * 18, y + 25, null);
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popAttrib();
    }
    
    public void gotPoolFromServer(ILootTablePool pool) {
        this.pool = pool;
    }
}
