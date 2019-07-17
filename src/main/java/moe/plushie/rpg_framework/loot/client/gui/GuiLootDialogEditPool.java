package moe.plushie.rpg_framework.loot.client.gui;

import java.io.IOException;
import java.util.ArrayList;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.loot.ILootTableItem;
import moe.plushie.rpg_framework.api.loot.ILootTablePool;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.GuiSlotHandler;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientLootEditorUpdate;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientLootEditorUpdate.LootEditType;
import moe.plushie.rpg_framework.loot.common.inventory.ContainerLootEditor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.Slot;
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
        slotHandler.initGui();
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
        try {
            updateSlots(false);
            slotHandler.mouseClicked(mouseX, mouseY, button);
            updateSlots(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        updateSlots(false);
        slotHandler.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        updateSlots(true);
    }
    
    @Override
    public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        updateSlots(false);
        slotHandler.mouseReleased(mouseX, mouseY, button);
        updateSlots(true);
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        try {
            updateSlots(false);
            slotHandler.keyTyped(c, keycode);
            updateSlots(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.keyTyped(c, keycode);
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
        
        updateSlots(false);
        slotHandler.drawScreen(mouseX, mouseY, partialTickTime);
        updateSlots(true);
        
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
    
    private void updateSlots(boolean restore) {
        ContainerLootEditor containerLootEditor = (ContainerLootEditor) slotHandler.inventorySlots;
        GuiLootEditor gui = (GuiLootEditor) parent;
        if (!restore) {
            ArrayList<Slot> playerSlots = containerLootEditor.getSlotsPlayer();
            int posX = x + 80 - gui.getGuiLeft();
            int posY = y + 158 - gui.getGuiTop();
            int playerInvY = posY;
            int hotBarY = playerInvY + 58;
            for (int ix = 0; ix < 9; ix++) {
                playerSlots.get(ix).xPos = posX + 18 * ix;
                playerSlots.get(ix).yPos = hotBarY;
            }
            for (int iy = 0; iy < 3; iy++) {
                for (int ix = 0; ix < 9; ix++) {
                    playerSlots.get(ix + iy * 9 + 9).xPos = posX + 18 * ix;
                    playerSlots.get(ix + iy * 9 + 9).yPos = playerInvY + iy * 18;
                }
            }
            //((SlotHidable)playerSlots.get(index))
        } else {
            ArrayList<Slot> playerSlots = containerLootEditor.getSlotsPlayer();
            int posX = -10000;
            int posY = -10000;
            int playerInvY = posY;
            int hotBarY = playerInvY + 58;
            for (int ix = 0; ix < 9; ix++) {
                playerSlots.get(ix).xPos = posX + 18 * ix;
                playerSlots.get(ix).yPos = hotBarY;
            }
            for (int iy = 0; iy < 3; iy++) {
                for (int ix = 0; ix < 9; ix++) {
                    playerSlots.get(ix + iy * 9 + 9).xPos = posX + 18 * ix;
                    playerSlots.get(ix + iy * 9 + 9).yPos = playerInvY + iy * 18;
                }
            }
        }
    }
}
