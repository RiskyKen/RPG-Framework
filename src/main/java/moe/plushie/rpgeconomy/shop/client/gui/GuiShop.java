package moe.plushie.rpgeconomy.shop.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import moe.plushie.rpgeconomy.api.core.IItemMatcher;
import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.api.currency.ICurrency.ICurrencyVariant;
import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.api.shop.IShop.IShopTab;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTab;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.inventory.slot.SlotCurrency;
import moe.plushie.rpgeconomy.core.common.lib.LibBlockNames;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiShopUpdate;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiShopUpdate.ShopMessageType;
import moe.plushie.rpgeconomy.shop.common.inventory.ContainerShop;
import moe.plushie.rpgeconomy.shop.common.tileentities.TileEntityShop;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiShop extends GuiTabbed implements IDialogCallback {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.SHOP);
    private static final int TEXTURE_SIZE_X = 176;
    private static final int TEXTURE_SIZE_Y = 152;

    private final EntityPlayer entityPlayer;
    private int activeTabIndex = 0;
    private IShop shop;

    protected AbstractGuiDialog dialog;
    int oldMouseX;
    int oldMouseY;

    private GuiIconButton buttonShopList;
    private GuiIconButton buttonStats;
    private GuiIconButton buttonEditMode;
    private GuiIconButton buttonRename;
    private GuiIconButton buttonSave;

    private GuiIconButton buttonEditTabAdd;
    private GuiIconButton buttonEditTabRemove;
    private GuiIconButton buttonEditTabEdit;
    private GuiIconButton buttonEditTabUp;
    private GuiIconButton buttonEditTabDown;

    private GuiIconButton[] buttonCostEdit;

    private boolean editMode = false;

    public GuiShop(EntityPlayer entityPlayer, TileEntityShop tileEntity) {
        super(new ContainerShop(entityPlayer, tileEntity), false);
        this.entityPlayer = entityPlayer;
        tabController.setActiveTabIndex(getActiveTab());
        tabChanged();
    }

    @Override
    public void initGui() {
        this.xSize = 320;
        this.ySize = 145;
        if (ConfigHandler.showPlayerInventoryInShopGUI) {
            this.ySize += 98 + 1;
        }
        super.initGui();
        if (isDialogOpen()) {
            dialog.initGui();
        }
        tabController.x = getGuiLeft() + 4;

        buttonShopList = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 147, 16, 16, TEXTURE);
        buttonStats = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 147 + 17 * 1, 16, 16, TEXTURE);
        buttonEditMode = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 147 + 17 * 2, 16, 16, TEXTURE);
        buttonRename = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 147 + 17 * 3, 16, 16, TEXTURE);
        buttonSave = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 147 + 17 * 4, 16, 16, TEXTURE);

        buttonEditTabAdd = new GuiIconButton(this, 0, getGuiLeft() + getXSize() - 16, getGuiTop() + 147 + 17 * 0, 16, 16, TEXTURE);
        buttonEditTabRemove = new GuiIconButton(this, 0, getGuiLeft() + getXSize() - 16, getGuiTop() + 147 + 17 * 1, 16, 16, TEXTURE);
        buttonEditTabEdit = new GuiIconButton(this, 0, getGuiLeft() + getXSize() - 16, getGuiTop() + 147 + 17 * 2, 16, 16, TEXTURE);
        buttonEditTabUp = new GuiIconButton(this, 0, getGuiLeft() + getXSize() - 16, getGuiTop() + 147 + 17 * 3, 16, 16, TEXTURE);
        buttonEditTabDown = new GuiIconButton(this, 0, getGuiLeft() + getXSize() - 16, getGuiTop() + 147 + 17 * 4, 16, 16, TEXTURE);

        buttonCostEdit = new GuiIconButton[8];
        for (int i = 0; i < 4; i++) {
            buttonCostEdit[i] = new GuiIconButton(this, 0, getGuiLeft() + 144, getGuiTop() + 20 + i * 31, 13, 9, TEXTURE);
            buttonCostEdit[i + 4] = new GuiIconButton(this, 0, getGuiLeft() + 280, getGuiTop() + 20 + i * 31, 13, 9, TEXTURE);
        }

        buttonShopList.setDrawButtonBackground(false).setIconLocation(208, 240, 16, 16);
        buttonStats.setDrawButtonBackground(false).setIconLocation(208, 224, 16, 16);
        buttonEditMode.setDrawButtonBackground(false).setIconLocation(208, 208, 16, 16);
        buttonRename.setDrawButtonBackground(false).setIconLocation(208, 192, 16, 16);
        buttonSave.setDrawButtonBackground(false).setIconLocation(208, 96, 16, 16);

        buttonEditTabAdd.setDrawButtonBackground(false).setIconLocation(208, 176, 16, 16);
        buttonEditTabRemove.setDrawButtonBackground(false).setIconLocation(208, 160, 16, 16);
        buttonEditTabEdit.setDrawButtonBackground(false).setIconLocation(208, 144, 16, 16);
        buttonEditTabUp.setDrawButtonBackground(false).setIconLocation(208, 128, 16, 16);
        buttonEditTabDown.setDrawButtonBackground(false).setIconLocation(208, 112, 16, 16);

        for (int i = 0; i < buttonCostEdit.length; i++) {
            buttonCostEdit[i].setDrawButtonBackground(false).setIconLocation(101, 91, 13, 9).setHoverText("Edit Cost");
        }

        buttonShopList.setHoverText("Shop List...");
        buttonStats.setHoverText("Stats...");
        buttonEditMode.setHoverText("Edit Mode").setDisableText(ChatFormatting.RED + "Shop must be LINKED to use this option.");
        buttonRename.setHoverText("Rename Shop...").setDisableText(ChatFormatting.RED + "Shop must be in EDIT MODE to use this option.");
        buttonSave.setHoverText("Save Shop").setDisableText(ChatFormatting.RED + "Shop must be in EDIT MODE to use this option.");

        buttonEditTabAdd.setHoverText("Add Tab");
        buttonEditTabRemove.setHoverText("Remove Tab");
        buttonEditTabEdit.setHoverText("Edit Tab");
        buttonEditTabUp.setHoverText("Move Tab Up");
        buttonEditTabDown.setHoverText("Move Tab Down");

        updateEditButtons();

        if (entityPlayer.capabilities.isCreativeMode) {
            buttonList.add(buttonShopList);
            buttonList.add(buttonStats);
            buttonList.add(buttonEditMode);
            buttonList.add(buttonRename);
            buttonList.add(buttonSave);

            buttonList.add(buttonEditTabAdd);
            buttonList.add(buttonEditTabRemove);
            buttonList.add(buttonEditTabEdit);
            buttonList.add(buttonEditTabUp);
            buttonList.add(buttonEditTabDown);

            for (int i = 0; i < buttonCostEdit.length; i++) {
                buttonList.add(buttonCostEdit[i]);
            }
        }
    }

    private void updateEditButtons() {
        buttonEditMode.enabled = isShopLinked();
        buttonStats.enabled = false;
        buttonRename.enabled = editMode;
        buttonSave.enabled = editMode;

        buttonEditTabAdd.enabled = editMode;
        buttonEditTabRemove.enabled = editMode;
        buttonEditTabEdit.enabled = editMode;
        buttonEditTabUp.enabled = editMode;
        buttonEditTabDown.enabled = editMode;

        for (int i = 0; i < buttonCostEdit.length; i++) {
            buttonCostEdit[i].visible = editMode;
            Slot slot = inventorySlots.inventorySlots.get(i);
            buttonCostEdit[i].enabled = slot.getHasStack();
        }

        if (isShopLinked()) {
            buttonStats.setHoverText("Stats").setDisableText(ChatFormatting.RED + "Coming soon. \u2122");
        } else {
            buttonStats.setDisableText(ChatFormatting.RED + "Shop must be LINKED to use this option.");
        }

        if (editMode) {
            buttonEditTabAdd.setDisableText(ChatFormatting.RED + "Tab list is full.");
            buttonEditTabRemove.setDisableText(ChatFormatting.RED + "Must have one tab to use this option.");
            buttonEditTabEdit.setDisableText(ChatFormatting.RED + "Must have one tab to use this option.");
            buttonEditTabUp.setDisableText(ChatFormatting.RED + "Already at top.");
            buttonEditTabDown.setHoverText("Move Tab Down").setDisableText(ChatFormatting.RED + "Already at bottom.");
        } else {
            buttonEditTabAdd.setDisableText(ChatFormatting.RED + "Shop must be in EDIT MODE to use this option.");
            buttonEditTabRemove.setDisableText(ChatFormatting.RED + "Shop must be in EDIT MODE to use this option.");
            buttonEditTabEdit.setDisableText(ChatFormatting.RED + "Shop must be in EDIT MODE to use this option.");
            buttonEditTabUp.setDisableText(ChatFormatting.RED + "Shop must be in EDIT MODE to use this option.");
            buttonEditTabDown.setDisableText(ChatFormatting.RED + "Shop must be in EDIT MODE to use this option.");
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        oldMouseX = mouseX;
        oldMouseY = mouseY;
        if (isDialogOpen()) {
            mouseX = mouseY = 0;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE);

        // Render shop background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21, getGuiTop(), 0, 0, 278, 145, 100, 100, 4, zLevel);

        // Render money background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21 + 177, getGuiTop() + 146, 0, 0, 101, 98, 100, 100, 4, zLevel);

        // Render title.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21 + 38, getGuiTop() + 4, 0, 132, 200, 13, 100, 13, 2, zLevel);

        // Render item box.
        for (int i = 0; i < 4; i++) {
            GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21 + 4, getGuiTop() + 18 + 31 * i, 0, 101, 134, 30, 134, 30, 2, zLevel);
            GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21 + 4 + 136, getGuiTop() + 18 + 31 * i, 0, 101, 134, 30, 134, 30, 2, zLevel);
        }

        // drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, TEXTURE_SIZE_X, TEXTURE_SIZE_Y);
        for (Slot slot : inventorySlots.inventorySlots) {
            if (slot instanceof SlotCurrency) {
                // drawTexturedModalRect(getGuiLeft() + slot.xPos - 1, getGuiTop() + slot.yPos - 1, 238, 0, 18, 18);
            }
        }
        if (ConfigHandler.showPlayerInventoryInShopGUI) {
            GuiHelper.renderPlayerInvTexture(getGuiLeft() + 21, getGuiTop() + 145 + 1);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        updateEditButtons();
        String title = "Loading...";
        int titleColour = 0x333333;
        if (shop != null) {
            title = shop.getName();
        }
        if (!isShopLinked()) {
            title = "SHOP NOT LINKED";
            titleColour = 0xAA0000;
        }
        if (editMode) {
            title = ChatFormatting.DARK_RED + "EDIT MODE - " + ChatFormatting.RESET + title + ChatFormatting.DARK_RED + " - EDIT MODE";
        }
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, xSize / 2 - titleWidth / 2, 6, titleColour);

        if (ConfigHandler.showPlayerInventoryInShopGUI) {
            GuiHelper.renderPlayerInvlabel(21, 145 + 1, fontRenderer);
        }

        for (int i = 0; i < 8; i++) {
            renderItemDetails(i);
        }

        fontRenderer.drawString("Player Munie!", 206, 151, 0x333333);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -guiTop, 0);
        for (GuiButton button : buttonList) {
            if (button instanceof GuiIconButton) {
                // ((GuiIconButton)button).drawRollover(mc, mouseX, mouseY);
            }
        }
        GlStateManager.popMatrix();

        if (isDialogOpen()) {
            GL11.glTranslatef(-guiLeft, -guiTop, 0);
            dialog.draw(oldMouseX, oldMouseY, 0);
            GL11.glTranslatef(guiLeft, guiTop, 0);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            if (button instanceof GuiIconButton) {
                ((GuiIconButton) button).drawRollover(mc, mouseX, mouseY);
            }
        }
        GL11.glPopMatrix();
    }

    private void renderItemDetails(int index) {
        Slot slot = inventorySlots.inventorySlots.get(index);
        if (!slot.getHasStack()) {
            return;
        }
        if (shop != null && activeTabIndex != -1) {
            if (shop.getTabCount() > activeTabIndex && index < shop.getTabs().get(activeTabIndex).getItemCount()) {
                fontRenderer.drawString("Stock: \u221E", slot.xPos + 23, slot.yPos - 4, 0x888888, false);
                ICost cost = shop.getTabs().get(activeTabIndex).getItems().get(index).getCost();
                renderCost(slot.xPos, slot.yPos, cost);
                // fontRenderer.drawString("Stock: " + amount, slot.xPos + 23, slot.yPos - 4, 0x888888, false);
            }
        }
    }

    private void renderCost(int slotX, int slotY, ICost cost) {
        if (cost.hasWalletCost()) {
            IWallet wallet = cost.getWalletCost();
            int amount = wallet.getAmount();
            boolean used = false;
            int renderCount = 0;
            for (int i = 0; i < wallet.getCurrency().getCurrencyVariants().length; i++) {
                if (amount > 0) {
                    ICurrencyVariant variant = wallet.getCurrency().getCurrencyVariants()[wallet.getCurrency().getCurrencyVariants().length - i - 1];
                    // variant = cost.getCurrency().getCurrencyVariants()[i];

                    int count = 0;
                    for (int j = 0; j < 22000; j++) {
                        if (variant.getValue() <= amount) {
                            amount -= variant.getValue();
                            count++;
                            used = true;
                        } else {
                            continue;
                        }
                    }

                    if (used) {
                        GlStateManager.pushMatrix();
                        GlStateManager.pushAttrib();
                        GlStateManager.translate(22 + slotX + renderCount * 17, 5 + slotY, 0);
                        // GlStateManager.scale(0.75, 0.75, 0.75);
                        ItemStack stack = variant.getItem().getItemStack().copy();
                        stack.setCount(1);
                        itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
                        if (count >= 1000) {
                            itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, 0, 0, String.valueOf(count / 1000) + "K");
                        } else {
                            itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, 0, 0, String.valueOf(count));
                        }
                        GlStateManager.popAttrib();
                        GlStateManager.popMatrix();
                        renderCount++;
                    }
                }
            }
        }
        if (cost.hasItemCost()) {
            IItemMatcher[] itemCost = cost.getItemCost();
            for (int i = 0; i < itemCost.length; i++) {
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();
                GlStateManager.translate(22 + slotX + i * 17, 5 + slotY, 0);
                // GlStateManager.scale(0.5, 0.5, 0.5);
                ItemStack stack = itemCost[i].getItemStack();
                // stack.setCount(1);
                itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
                itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, 0, 0, String.valueOf(stack.getCount()));
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
        }
    }

    public boolean isShopLinked() {
        return shop != null;
    }

    public void gotShopFromServer(IShop shop, boolean update) {
        this.shop = shop;
        tabController.clearTabs();
        if (isShopLinked()) {
            for (int i = 0; i < shop.getTabCount(); i++) {
                IShopTab shopTab = shop.getTabs().get(i);
                int y = MathHelper.floor(shopTab.getIconIndex() / 16);
                int x = shopTab.getIconIndex() - (y * 16);
                tabController.addTab(new GuiTab(tabController, shopTab.getName()).setIconLocation(x * 16, y * 16).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
            }
        } else {
            editMode = false;
        }

        ((ContainerShop) inventorySlots).setShop(shop);
        updateEditButtons();

        if (!update) {
            activeTabIndex = 0;
            tabController.setActiveTabIndex(getActiveTab());
        } else {
            setActiveTab(getActiveTab());
            tabController.setActiveTabIndex(activeTabIndex);
        }
        tabChanged();
    }

    public void gotShopIdentifiersFromServer(String[] shopIdentifiers) {
        if (isDialogOpen() && dialog instanceof GuiShopDialogShopList) {
            ((GuiShopDialogShopList) dialog).gotShopIdentifiersFromServer(shopIdentifiers);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button == buttonEditMode) {
            setEditMode(!editMode);
        }
        if (button == buttonShopList) {
            openDialog(new GuiShopDialogShopList(this, "shopList", this, 310, 230));
        }
        if (button == buttonRename) {
            openDialog(new GuiShopDialogRename(this, "shopRename", this, 190, 100, shop.getName()));
        }
        if (button == buttonSave) {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.SHOP_SAVE));
        }
        if (button == buttonEditTabAdd) {
            openDialog(new GuiShopDialogTabAdd(this, "tabAdd", this, 190, 100));
        }
        if (button == buttonEditTabRemove) {
            if (activeTabIndex == -1) {
                return;
            }
            openDialog(new GuiShopDialogTabRemove(this, "tabRemove", this, 190, 100, shop.getTabs().get(activeTabIndex)));
        }
        if (button == buttonEditTabEdit) {
            if (activeTabIndex == -1) {
                return;
            }
            openDialog(new GuiShopDialogTabEdit(this, "tabEdit", this, 190, 100, shop.getTabs().get(activeTabIndex)));
        }
        if (button == buttonEditTabUp) {
            if (activeTabIndex == -1) {
                return;
            }
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.TAB_MOVE_UP));
            setActiveTab(getActiveTab() - 1);
            tabController.setActiveTabIndex(activeTabIndex);
        }
        if (button == buttonEditTabDown) {
            if (activeTabIndex == -1) {
                return;
            }
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.TAB_MOVE_DOWN));
            setActiveTab(getActiveTab() + 1);
            tabController.setActiveTabIndex(activeTabIndex);
        }
        for (int i = 0; i < buttonCostEdit.length; i++) {
            if (activeTabIndex == -1) {
                return;
            }
            if (button == buttonCostEdit[i]) {
                openDialog(new GuiShopDialogEditCost(this, "editCost", this, 310, 230, i, shop.getTabs().get(activeTabIndex).getItems().get(i).getCost()));
            }
        }
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.OK) {
            if (dialog instanceof GuiShopDialogShopList) {
                String shopIdentifier = ((GuiShopDialogShopList) dialog).getSelectedShopIdentifier();
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.SHOP_CHANGE).setShopIdentifier(shopIdentifier));
            }
            if (dialog instanceof GuiShopDialogRename) {
                String shopName = ((GuiShopDialogRename) dialog).getShopName();
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.SHOP_RENAME).setShopName(shopName));
            }
            if (dialog instanceof GuiShopDialogTabAdd) {
                String name = ((GuiShopDialogTabAdd) dialog).getTabName();
                int iconIndex = ((GuiShopDialogTabAdd) dialog).getTabIconIndex();
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.TAB_ADD).setTabName(name).setTabIconIndex(iconIndex));
            }
            if (dialog instanceof GuiShopDialogTabRemove) {
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.TAB_REMOVE).setTabIndex(activeTabIndex));
                setActiveTab(getActiveTab());
                tabController.setActiveTabIndex(activeTabIndex);
            }
            if (dialog instanceof GuiShopDialogTabEdit) {
                String name = ((GuiShopDialogTabEdit) dialog).getTabName();
                int iconIndex = ((GuiShopDialogTabEdit) dialog).getTabIconIndex();
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.TAB_EDIT).setTabName(name).setTabIconIndex(iconIndex));
            }
        }
        this.dialog = null;
    }

    private void setEditMode(boolean editMode) {
        this.editMode = editMode;
        updateEditButtons();
        if (editMode) {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.EDIT_MODE_ON));
            ((ContainerShop) inventorySlots).setEditMode(true);
        } else {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.EDIT_MODE_OFF));
            ((ContainerShop) inventorySlots).setEditMode(false);
        }
    }

    @Override
    public String getName() {
        return LibBlockNames.SHOP;
    }

    @Override
    protected int getActiveTab() {
        return activeTabIndex;
    }

    @Override
    protected void setActiveTab(int value) {
        if (tabController.getTabCount() > 0) {
            activeTabIndex = MathHelper.clamp(value, 0, tabController.getTabCount() - 1);
        } else {
            activeTabIndex = -1;
        }
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.TAB_CHANGED).setTabIndex(activeTabIndex));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (isDialogOpen()) {
            dialog.mouseClicked(mouseX, mouseY, button);
        } else {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if (isDialogOpen()) {
            dialog.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        } else {
            super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (isDialogOpen()) {
            dialog.mouseMovedOrUp(mouseX, mouseY, state);
        } else {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char c, int keycode) throws IOException {
        if (isDialogOpen()) {
            dialog.keyTyped(c, keycode);
        } else {
            super.keyTyped(c, keycode);
        }
    }

    public void openDialog(AbstractGuiDialog dialog) {
        this.dialog = dialog;
        dialog.initGui();
    }

    protected boolean isDialogOpen() {
        return dialog != null;
    }
}
