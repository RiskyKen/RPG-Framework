package moe.plushie.rpg_framework.shop.client.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.ICurrencyCapability;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.api.shop.IShop;
import moe.plushie.rpg_framework.api.shop.IShop.IShopTab;
import moe.plushie.rpg_framework.api.shop.IShop.IShopTab.TabType;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTab;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotCurrency;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.core.common.lib.LibBlockNames;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiShopUpdate;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiShopUpdate.ShopMessageType;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.CurrencyManager;
import moe.plushie.rpg_framework.currency.common.CurrencyWalletHelper;
import moe.plushie.rpg_framework.currency.common.Wallet;
import moe.plushie.rpg_framework.currency.common.capability.CurrencyCapability;
import moe.plushie.rpg_framework.shop.common.inventory.ContainerShop;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiShop extends GuiTabbed<ContainerShop> implements IDialogCallback {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.SHOP);
    private static final int TEXTURE_SIZE_X = 176;
    private static final int TEXTURE_SIZE_Y = 152;

    private final EntityPlayer entityPlayer;
    private final boolean hasTile;
    private int activeTabIndex = 0;
    private IShop shop;

    private GuiIconButton buttonShopList;
    private GuiIconButton buttonStats;
    private GuiIconButton buttonEditMode;
    private GuiIconButton buttonRename;
    // private GuiIconButton buttonSave;

    private GuiIconButton buttonEditTabAdd;
    private GuiIconButton buttonEditTabRemove;
    private GuiIconButton buttonEditTabEdit;
    private GuiIconButton buttonEditTabUp;
    private GuiIconButton buttonEditTabDown;

    private GuiIconButton[] buttonCostEdit;

    private boolean editMode = false;

    public GuiShop(EntityPlayer entityPlayer, boolean hasTile) {
        super(new ContainerShop(entityPlayer, null, null), false);
        this.entityPlayer = entityPlayer;
        this.hasTile = hasTile;
        tabController.setActiveTabIndex(getActiveTab());
        tabChanged();

        ContainerShop containerShop = (ContainerShop) inventorySlots;
        for (Slot slot : containerShop.getSlotsPrice()) {
            ((SlotHidable) slot).setVisible(false);
        }
    }

    @Override
    public void initGui() {
        this.xSize = 320;
        this.ySize = 145;
        if (ConfigHandler.options.showPlayerInventoryInShopGUI) {
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
        // buttonSave = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 147 + 17 * 4, 16, 16, TEXTURE);

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
        // buttonSave.setDrawButtonBackground(false).setIconLocation(208, 96, 16, 16);

        buttonEditTabAdd.setDrawButtonBackground(false).setIconLocation(208, 176, 16, 16);
        buttonEditTabRemove.setDrawButtonBackground(false).setIconLocation(208, 160, 16, 16);
        buttonEditTabEdit.setDrawButtonBackground(false).setIconLocation(208, 144, 16, 16);
        buttonEditTabUp.setDrawButtonBackground(false).setIconLocation(208, 128, 16, 16);
        buttonEditTabDown.setDrawButtonBackground(false).setIconLocation(208, 112, 16, 16);

        for (int i = 0; i < buttonCostEdit.length; i++) {
            buttonCostEdit[i].setDrawButtonBackground(false).setIconLocation(101, 91, 13, 9).setHoverText(GuiHelper.getLocalControlName(getName(), "button.edit_cost"));
        }

        buttonShopList.setHoverText(GuiHelper.getLocalControlName(getName(), "button.shop_list"));
        buttonShopList.setDisableText(GuiHelper.getLocalControlName(getName(), "button.shop_list.no_block"));

        buttonStats.setHoverText(GuiHelper.getLocalControlName(getName(), "button.stats"));
        buttonEditMode.setHoverText(GuiHelper.getLocalControlName(getName(), "button.edit_mode"));
        buttonEditMode.setDisableText(GuiHelper.getLocalControlName(getName(), "common.not_linked"));
        buttonRename.setHoverText(GuiHelper.getLocalControlName(getName(), "button.rename_shop"));
        buttonRename.setDisableText(GuiHelper.getLocalControlName(getName(), "common.need_edit_mode"));

        // buttonSave.setHoverText(GuiHelper.getLocalControlName(getName(), "button.save");
        // buttonSave.setDisableText(GuiHelper.getLocalControlName(getName(), "common.need_edit_mode");

        buttonEditTabAdd.setHoverText(GuiHelper.getLocalControlName(getName(), "button.tab_add"));
        buttonEditTabRemove.setHoverText(GuiHelper.getLocalControlName(getName(), "button.tab_remove"));
        buttonEditTabEdit.setHoverText(GuiHelper.getLocalControlName(getName(), "button.tab_edit"));
        buttonEditTabUp.setHoverText(GuiHelper.getLocalControlName(getName(), "button.tab_move_up"));
        buttonEditTabDown.setHoverText(GuiHelper.getLocalControlName(getName(), "button.tab_move_down"));

        updateEditButtons();

        if (entityPlayer.capabilities.isCreativeMode) {
            buttonList.add(buttonShopList);
            buttonList.add(buttonStats);
            buttonList.add(buttonEditMode);
            buttonList.add(buttonRename);
            // buttonList.add(buttonSave);

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
        buttonShopList.enabled = hasTile;
        buttonEditMode.enabled = isShopLinked();
        buttonStats.enabled = false;
        buttonRename.enabled = editMode;
        // buttonSave.enabled = editMode;

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

        if (!isShopLinked()) {
            buttonStats.setDisableText(GuiHelper.getLocalControlName(getName(), "common.not_linked"));
        } else {
            buttonStats.setDisableText(ChatFormatting.RED + "Coming soon. \u2122");
        }

        if (editMode) {
            buttonEditTabAdd.setDisableText(GuiHelper.getLocalControlName(getName(), "button.tab_add.full"));
            buttonEditTabRemove.setDisableText(GuiHelper.getLocalControlName(getName(), "button.tab_remove.empty"));
            buttonEditTabEdit.setDisableText(GuiHelper.getLocalControlName(getName(), "button.tab_edit.empty"));
            buttonEditTabUp.setDisableText(GuiHelper.getLocalControlName(getName(), "button.tab_move_up.at_top"));
            buttonEditTabDown.setDisableText(GuiHelper.getLocalControlName(getName(), "button.tab_move_down.at_bottom"));
        } else {
            buttonEditTabAdd.setDisableText(GuiHelper.getLocalControlName(getName(), "common.need_edit_mode"));
            buttonEditTabRemove.setDisableText(GuiHelper.getLocalControlName(getName(), "common.need_edit_mode"));
            buttonEditTabEdit.setDisableText(GuiHelper.getLocalControlName(getName(), "common.need_edit_mode"));
            buttonEditTabUp.setDisableText(GuiHelper.getLocalControlName(getName(), "common.need_edit_mode"));
            buttonEditTabDown.setDisableText(GuiHelper.getLocalControlName(getName(), "common.need_edit_mode"));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE);

        // Render shop background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21, getGuiTop(), 0, 0, 278, 145, 100, 100, 4, zLevel);

        // Render money background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 19 + 174, getGuiTop() + 146, 0, 0, 111, 98, 100, 100, 4, zLevel);

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
        if (ConfigHandler.options.showPlayerInventoryInShopGUI) {
            GuiHelper.renderPlayerInvTexture(getGuiLeft() + 16, getGuiTop() + 145 + 1);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        updateEditButtons();
        String title = GuiHelper.getLocalControlName(getName(), "name.loading");
        if (shop != null) {
            title = shop.getName();
        }
        if (!isShopLinked()) {
            title = GuiHelper.getLocalControlName(getName(), "name.not_linked");
        }
        if (editMode) {
            title = GuiHelper.getLocalControlName(getName(), "name.edit_mode", title);
        }
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, xSize / 2 - titleWidth / 2, 6, 0x333333);

        if (ConfigHandler.options.showPlayerInventoryInShopGUI) {
            GuiHelper.renderPlayerInvlabel(16, 145 + 1, fontRenderer);
        }

        // Render shop item labels.
        for (int i = 0; i < 8; i++) {
            renderItemDetails(i);
        }

        fontRenderer.drawString(GuiHelper.getLocalControlName(getName(), "label.player_money"), 200, 151, 0x333333);
        CurrencyManager currencyManager = RPGFramework.getProxy().getCurrencyManager();
        ICurrency[] currencies = currencyManager.getCurrencies();
        int yCur = 0;
        ICurrencyCapability cap = CurrencyCapability.get(entityPlayer);
        for (int i = 0; i < currencies.length; i++) {
            ICurrency currency = currencies[i];
            int amount = 0;
            if (!currency.getCurrencyWalletInfo().getNeedItemToAccess() | CurrencyWalletHelper.haveWalletForCurrency(entityPlayer, currency)) {
                IWallet playerWallet = cap.getWallet(currency);
                amount += playerWallet.getAmount();
            }
            amount += CurrencyWalletHelper.getAmountInInventory(currency, entityPlayer.inventory);
            if (amount > 0) {
                GuiHelper.renderCost(fontRenderer, itemRender, new Cost(new Wallet(currency, amount), null), 176, 156 + yCur * 18);
                yCur++;
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -guiTop, 0);
        for (GuiButton button : buttonList) {
            if (button instanceof GuiIconButton) {
                // ((GuiIconButton)button).drawRollover(mc, mouseX, mouseY);
            }
        }
        GlStateManager.popMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = buttonList.get(i);
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
                GuiHelper.renderCost(fontRenderer, itemRender, cost, slot.xPos, slot.yPos, cost.canAfford(entityPlayer));
                // fontRenderer.drawString("Stock: " + amount, slot.xPos + 23, slot.yPos - 4, 0x888888, false);
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

    public void gotShopIdentifiersFromServer(IIdentifier[] shopIdentifiers, String[] shopNames) {
        if (isDialogOpen() && dialog instanceof GuiShopDialogShopList) {
            ((GuiShopDialogShopList) dialog).gotShopIdentifiersFromServer(shopIdentifiers, shopNames);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button == buttonEditMode) {
            setEditMode(!editMode);
        }
        if (button == buttonShopList) {
            openDialog(new GuiShopDialogShopList(this, GuiHelper.getLocalControlName(getName(), "dialog.shop_list"), this));
        }
        if (button == buttonRename) {
            openDialog(new GuiShopDialogRename(this, GuiHelper.getLocalControlName(getName(), "dialog.shop_rename"), this, 190, 100, shop.getName()));
        }
        // if (button == buttonSave) {
        // PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.SHOP_SAVE));
        // }
        if (button == buttonEditTabAdd) {
            openDialog(new GuiShopDialogTabAdd(this, GuiHelper.getLocalControlName(getName(), "dialog.tab_add"), this));
        }
        if (button == buttonEditTabRemove) {
            if (activeTabIndex == -1) {
                return;
            }
            openDialog(new GuiShopDialogTabRemove(this, GuiHelper.getLocalControlName(getName(), "dialog.tab_remove"), this, 190, 100, shop.getTabs().get(activeTabIndex)));
        }
        if (button == buttonEditTabEdit) {
            if (activeTabIndex == -1) {
                return;
            }
            openDialog(new GuiShopDialogTabEdit(this, GuiHelper.getLocalControlName(getName(), "dialog.tab_edit"), this, shop.getTabs().get(activeTabIndex)));
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
                // ((ContainerShop)inventorySlots).gotCostRequest(i);
                openDialog(new GuiShopDialogEditCost(this, GuiHelper.getLocalControlName(getName(), "dialog.cost_edit"), this, 210, 120, i, shop.getTabs().get(activeTabIndex).getItems().get(i).getCost()));
            }
        }
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.OK) {
            if (dialog instanceof GuiShopDialogShopList) {
                IIdentifier shopIdentifier = ((GuiShopDialogShopList) dialog).getSelectedShopIdentifier();
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.SHOP_CHANGE).setShopIdentifier(shopIdentifier));
            }
            if (dialog instanceof GuiShopDialogRename) {
                String shopName = ((GuiShopDialogRename) dialog).getShopName();
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.SHOP_RENAME).setShopName(shopName));
            }
            if (dialog instanceof GuiShopDialogTabAdd) {
                String name = ((GuiShopDialogTabAdd) dialog).getTabName();
                int iconIndex = ((GuiShopDialogTabAdd) dialog).getTabIconIndex();
                TabType tabType = ((GuiShopDialogTabAdd) dialog).getTabType();
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.TAB_ADD).setTabName(name).setTabIconIndex(iconIndex).setTabType(tabType));
            }
            if (dialog instanceof GuiShopDialogTabRemove) {
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.TAB_REMOVE).setTabIndex(activeTabIndex));
                setActiveTab(getActiveTab());
                tabController.setActiveTabIndex(activeTabIndex);
            }
            if (dialog instanceof GuiShopDialogTabEdit) {
                String name = ((GuiShopDialogTabEdit) dialog).getTabName();
                int iconIndex = ((GuiShopDialogTabEdit) dialog).getTabIconIndex();
                TabType tabType = ((GuiShopDialogTabEdit) dialog).getTabType();
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.TAB_EDIT).setTabName(name).setTabIconIndex(iconIndex).setTabType(tabType));
            }
            if (dialog instanceof GuiShopDialogEditCost) {
                ICost cost = ((GuiShopDialogEditCost) dialog).getCost();
                int slotIndex = ((GuiShopDialogEditCost) dialog).getSlotIndex();
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.ITEM_UPDATE).setCost(slotIndex, cost));
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
}
