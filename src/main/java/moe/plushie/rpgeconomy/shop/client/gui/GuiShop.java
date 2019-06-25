package moe.plushie.rpgeconomy.shop.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpgeconomy.api.currency.ICurrency.ICurrencyVariant;
import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.api.shop.IShop.IShopTab;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog.DialogResult;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog.IDialogCallback;
import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTab;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabController.ITabEditCallback;
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
public class GuiShop extends GuiTabbed implements IDialogCallback, ITabEditCallback {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.SHOP);
    private static final int TEXTURE_SIZE_X = 176;
    private static final int TEXTURE_SIZE_Y = 152;
    
    private final EntityPlayer entityPlayer;
    private int activeTabIndex = 0;
    private IShop shop;
    private boolean shopLinked = false;
    
    protected AbstractGuiDialog dialog;
    int oldMouseX;
    int oldMouseY;
    
    private GuiIconButton buttonEditMode;
    private GuiIconButton buttonShopList;
    private GuiIconButton buttonSave;
    
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
        
        buttonEditMode = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 147, 16, 16, TEXTURE).setDrawButtonBackground(true).setHoverText("Edit Mode");
        buttonShopList = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 167, 16, 16, TEXTURE).setDrawButtonBackground(true).setHoverText("Shop List");
        buttonSave = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 187, 16, 16, TEXTURE).setDrawButtonBackground(true).setHoverText("Save");
        
        buttonList.add(buttonEditMode);
        buttonList.add(buttonShopList);
        buttonList.add(buttonSave);
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
        
        //drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, TEXTURE_SIZE_X, TEXTURE_SIZE_Y);
        for (Slot slot : inventorySlots.inventorySlots) {
            if (slot instanceof SlotCurrency) {
                //drawTexturedModalRect(getGuiLeft() + slot.xPos - 1, getGuiTop() + slot.yPos - 1, 238, 0, 18, 18);
            }
        }
        if (ConfigHandler.showPlayerInventoryInShopGUI) {
            GuiHelper.renderPlayerInvTexture(getGuiLeft() + 21, getGuiTop() + 145 + 1);
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = "Loading...";
        int titleColour = 0x333333;
        if (shop != null) {
            title = shop.getName();
        }
        if (!shopLinked) {
            title = "SHOP NOT LINKED";
            titleColour = 0xAA0000;
        }
        if (editMode) {
            title = "EDIT MODE - " + title + " - EDIT MODE";
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
                //((GuiIconButton)button).drawRollover(mc, mouseX, mouseY);
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
                ((GuiIconButton)button).drawRollover(mc, mouseX, mouseY);
            }
        }
        GL11.glPopMatrix();
    }
    
    private void renderItemDetails(int index) {
        Slot slot = inventorySlots.inventorySlots.get(index);
        if (shop != null && activeTabIndex != -1) {
            if (shop.getTabCount() > activeTabIndex && index < shop.getTabs()[activeTabIndex].getItemCount()) {
                fontRenderer.drawString("Stock: \u221E", slot.xPos + 23, slot.yPos - 4, 0x888888, false);
                IWallet cost = shop.getTabs()[activeTabIndex].getItems()[index].getCost();
                int amount = cost.getAmount();
                boolean used = false;
                int renderCount = 0;
                //fontRenderer.drawString("Stock: " + amount, slot.xPos + 23, slot.yPos - 4, 0x888888, false);
                for (int i = 0; i < cost.getCurrency().getCurrencyVariants().length; i++) {
                    if (amount > 0) {
                        ICurrencyVariant variant = cost.getCurrency().getCurrencyVariants()[cost.getCurrency().getCurrencyVariants().length - i - 1];
                        //variant = cost.getCurrency().getCurrencyVariants()[i];

                        int count = 0;
                        for (int j = 0; j < 64; j++) {
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
                            GlStateManager.translate(108 + slot.xPos + renderCount * -17, 5 + slot.yPos, 0);
                            // GlStateManager.scale(0.5, 0.5, 0.5);
                            ItemStack stack = variant.getItem().copy();
                            stack.setCount(1);
                            itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
                            itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, 0, 0, String.valueOf(count));
                            GlStateManager.popAttrib();
                            GlStateManager.popMatrix();
                            renderCount++;
                        }
                    }
                }
            }
        }
    }
    
    public void gotShopFromServer(IShop shop) {
        this.shop = shop;
        shopLinked = shop != null;
        tabController.clearTabs();
        if (shop != null) {
            for (int i = 0; i < shop.getTabCount(); i++) {
                IShopTab shopTab = shop.getTabs()[i];
                int y = MathHelper.floor(shopTab.getIconIndex() / 16);
                int x = shopTab.getIconIndex() - (y * 16);
                tabController.addTab(new GuiTab(tabController, shopTab.getName()).setIconLocation(x * 16, y * 16).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
            }
        }
        activeTabIndex = 0;
        tabController.setActiveTabIndex(getActiveTab());
        tabChanged();
    }
    
    public void gotShopIdentifiersFromServer(String[] shopIdentifiers) {
        if (isDialogOpen() && dialog instanceof GuiShopDialogShopList) {
            ((GuiShopDialogShopList)dialog).gotShopIdentifiersFromServer(shopIdentifiers);
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button == buttonEditMode) {
            setEditMode(!editMode);
        }
        if (button == buttonShopList) {
            openDialog(new GuiShopDialogShopList(this, "shopList", this, 300, 200));
        }
    }
    
    private void setEditMode(boolean editMode) {
        this.editMode = editMode;
        tabController.setEditMode(editMode);
        if (editMode) {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.EDIT_MODE_ON));
        } else {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.EDIT_MODE_OFF));
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
        activeTabIndex = value;
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.TAB_CHANGED).setTabIndex(value));
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

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.OK) {
            if (dialog instanceof GuiShopDialogShopList) {
                String shopIdentifier = ((GuiShopDialogShopList)dialog).getSelectedShopIdentifier();
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.SHOP_CHANGE).setShopIdentifier(shopIdentifier));
            }
        }
        this.dialog = null;
    }

    @Override
    public void tabAdded() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tabRemoved(int index) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tabMovedBack(int index) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tabMovedForward(int index) {
        // TODO Auto-generated method stub
        
    }
}
