package moe.plushie.rpg_framework.loot.client.gui;

import java.io.IOException;
import java.util.ArrayList;

import moe.plushie.rpg_framework.api.loot.ILootTableItem;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiLabeledTextField;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientBasicLootUpdate;
import moe.plushie.rpg_framework.loot.common.LootTableItem;
import moe.plushie.rpg_framework.loot.common.inventory.ContainerBasicLootBag;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBasicLootBag extends GuiContainer {

    protected static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(LibGuiResources.BACKGROUND);
    protected static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.BUTTONS);
    protected static final ResourceLocation TEXTURE_ICONS = new ResourceLocation(LibGuiResources.ICONS);

    private final EntityPlayer player;
    private final ItemStack stack;

    private GuiLabeledTextField textWeight;

    private int activeSlot;

    public GuiBasicLootBag(EntityPlayer player, ItemStack stack) {
        super(new ContainerBasicLootBag(player, stack));
        this.player = player;
        this.stack = stack;
    }

    @Override
    public void initGui() {
        this.xSize = 300;
        this.ySize = 240;
        super.initGui();
        buttonList.clear();

        textWeight = new GuiLabeledTextField(fontRenderer, getGuiLeft() + 125, getGuiTop() + 33, 100, 14);
        textWeight.setEmptyLabel("Enter Weight");

        changeActiveSlot(0);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isShiftKeyDown()) {
            for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
                if (i > 35) {
                    break;
                }
                Slot slot = this.inventorySlots.inventorySlots.get(i);
                if (isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY)) {
                    if (mouseButton == 0) {
                        changeActiveSlot(i);
                    }
                    if (mouseButton == 1) {
                        weightChanged("0", i);
                    }
                    return;
                }
            }
        }
        textWeight.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 1 & textWeight.isFocused()) {
            textWeight.setText("");
            weightChanged(textWeight.getText());
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (Character.isDigit(typedChar) | keyCode == 14) {
            if (textWeight.textboxKeyTyped(typedChar, keyCode)) {
                weightChanged(textWeight.getText());
                return;
            }
        }
        super.keyTyped(typedChar, keyCode);
    }
    
    private void weightChanged(String value, int slotIndex) {
        int weight = 0;
        try {
            weight = Integer.parseInt(value);
        } catch (Exception e) {
        }
        ArrayList<ILootTableItem> tableItems = ((ContainerBasicLootBag) inventorySlots).getTableItems();
        tableItems.set(slotIndex, new LootTableItem(tableItems.get(slotIndex).getItem(), weight));
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientBasicLootUpdate(slotIndex, weight));
    }

    private void weightChanged(String value) {
        weightChanged(value, activeSlot);
    }

    private void changeActiveSlot(int activeSlot) {
        this.activeSlot = activeSlot;
        ArrayList<ILootTableItem> tableItems = ((ContainerBasicLootBag) inventorySlots).getTableItems();
        textWeight.setText(String.valueOf(tableItems.get(activeSlot).getWeight()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_BACKGROUND);
        // Main background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize() - 99, 64, 64, 5, zLevel);

        // Title box.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 5, getGuiTop() + 4, 0, 64, getXSize() - 10, 14, 64, 13, 2, zLevel);

        // Slots Box.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 5, getGuiTop() + 20, 0, 64, 114, 115, 64, 13, 2, zLevel);
        ArrayList<ILootTableItem> tableItems = ((ContainerBasicLootBag) inventorySlots).getTableItems();
        for (int iy = 0; iy < ContainerBasicLootBag.BAG_HEIGHT; iy++) {
            for (int ix = 0; ix < ContainerBasicLootBag.BAG_WIDTH; ix++) {
                int index = ix + (iy * ContainerBasicLootBag.BAG_WIDTH);

                ILootTableItem item = tableItems.get(index);
                GlStateManager.color(1, 1, 1, 1);
                if (item.getWeight() > 0) {
                    GlStateManager.color(0, 1, 0, 1);
                }

                drawTexturedModalRect(getGuiLeft() + 8 + ix * 18, getGuiTop() + 23 + iy * 18, 220, 0, 18, 18);
                if (index == activeSlot) {
                    drawTexturedModalRect(getGuiLeft() + 8 + ix * 18, getGuiTop() + 23 + iy * 18, 238, 0, 18, 18);
                }
                GlStateManager.color(1, 1, 1, 1);
                // fontRenderer.drawString("" + index, getGuiLeft() + 8 + ix * 18, getGuiTop() + 23 + iy * 18, 0x333333);
                // mc.renderEngine.bindTexture(TEXTURE_BACKGROUND);
            }
        }

        GlStateManager.color(1, 1, 1, 1);

        // Other Box.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 121, getGuiTop() + 20, 0, 64, 174, 115, 64, 13, 2, zLevel);

        // Player inv background.
        GuiHelper.renderPlayerInvTexture(getGuiLeft(), getGuiTop() + getYSize() - 98);

        textWeight.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderGuiName(fontRenderer, xSize, stack.getDisplayName());

        GuiHelper.renderPlayerInvlabel(0, getYSize() - 98, fontRenderer);

        fontRenderer.drawString("Weight:", 125, 24, 0x333333);
    }
}
