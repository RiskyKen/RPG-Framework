package moe.plushie.rpgeconomy.shop.common.inventory;

import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.api.shop.IShop.IShopItem;
import moe.plushie.rpgeconomy.api.shop.IShop.IShopTab;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.inventory.ModTileContainer;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerShop;
import moe.plushie.rpgeconomy.shop.common.Shop.ShopItem;
import moe.plushie.rpgeconomy.shop.common.Shop.ShopTab;
import moe.plushie.rpgeconomy.shop.common.inventory.slot.SlotShop;
import moe.plushie.rpgeconomy.shop.common.tileentities.TileEntityShop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ContainerShop extends ModTileContainer<TileEntityShop> {

    private final InventoryBasic inventory;
    private IShop shop;
    private boolean editMode = false;
    private int activeTabIndex = 0;

    public ContainerShop(EntityPlayer entityPlayer, TileEntityShop tileEntity) {
        super(entityPlayer, tileEntity);
        inventory = new InventoryBasic("shop", false, 8);

        if (!entityPlayer.getEntityWorld().isRemote) {
            setShopFromTile();
        }

        for (int i = 0; i < 4; i++) {
            addSlotToContainer(new SlotShop(inventory, i, 32, 25 + i * 31, this));
        }

        for (int i = 0; i < 4; i++) {
            addSlotToContainer(new SlotShop(inventory, i + 4, 168, 25 + i * 31, this));
        }

        if (ConfigHandler.showPlayerInventoryInShopGUI) {
            addPlayerSlots(29, 162);
        }
    }

    private void setShopFromTile() {
        this.shop = tileEntity.getShop();
        changeTab(0);
    }

    private void setSlotForTab() {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            inventory.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        if (shop != null && shop.getTabCount() > 0) {
            if (activeTabIndex != -1) {
                for (int i = 0; i < shop.getTabs().get(activeTabIndex).getItemCount(); i++) {
                    if (i < 8) {
                        inventory.setInventorySlotContents(i, shop.getTabs().get(activeTabIndex).getItems().get(i).getItem());
                    }
                }
            }
        }
    }

    public IShop getShop() {
        return shop;
    }

    public void setShop(IShop shop) {
        this.shop = shop;
    }

    public void onSlotChanged(int slotIndex, ItemStack stack) {
        if (!isEditMode()) {
            return;
        }
        if (shop == null || shop.getTabCount() == 0) {
            return;
        }
        if (activeTabIndex == -1) {
            return;
        }

        IShopTab shopTab = shop.getTabs().get(activeTabIndex);
        ICost cost = shopTab.getItems().get(slotIndex).getCost();

        if (stack.isEmpty()) {
            shopTab.getItems().set(slotIndex, new ShopItem(ItemStack.EMPTY, cost));
        } else {
            shopTab.getItems().set(slotIndex, new ShopItem(stack.copy(), cost));
        }
    }

    public void tabAdd(int iconIndex, String tabName) {
        shop.getTabs().add(new ShopTab(tabName, iconIndex));
        sendShopToListeners(true);
    }

    public void tabEdit(int iconIndex, String tabName) {
        IShopTab tabOld = shop.getTabs().get(activeTabIndex);
        shop.getTabs().set(activeTabIndex, new ShopTab(tabName, iconIndex, tabOld.getItems()));
        sendShopToListeners(true);
    }

    public void tabRemove(int index) {
        shop.getTabs().remove(index);
        sendShopToListeners(true);
    }

    public void tabMove(boolean up) {
        if (activeTabIndex == -1) {
            return;
        }
        if (!up) {
            if (activeTabIndex < shop.getTabCount() - 1) {
                IShopTab tab1 = shop.getTabs().get(activeTabIndex);
                IShopTab tab2 = shop.getTabs().get(activeTabIndex + 1);
                shop.getTabs().set(activeTabIndex, tab2);
                shop.getTabs().set(activeTabIndex + 1, tab1);
                sendShopToListeners(true);
            }
        } else {
            if (activeTabIndex > 0) {
                IShopTab tab1 = shop.getTabs().get(activeTabIndex);
                IShopTab tab2 = shop.getTabs().get(activeTabIndex - 1);
                shop.getTabs().set(activeTabIndex - 1, tab1);
                shop.getTabs().set(activeTabIndex, tab2);
                sendShopToListeners(true);
            }
        }
    }

    public void saveShop() {
        RpgEconomy.getProxy().getShopManager().saveShop(shop);
    }

    public void shopRename(String shopName) {
        shop.setName(shopName);
        sendShopToListeners(true);
    }
    
    public void updateItem(int slotIndex, ICost cost) {
        IShopItem shopItem = shop.getTabs().get(activeTabIndex).getItems().get(slotIndex);
        shopItem = new ShopItem(shopItem.getItem(), cost);
        shop.getTabs().get(activeTabIndex).getItems().set(slotIndex, shopItem);
        sendShopToListeners(true);
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void changeTab(int index) {
        if (shop != null) {
            activeTabIndex = MathHelper.clamp(index, 0, shop.getTabCount() - 1);
        } else {
            activeTabIndex = -1;
        }
        setSlotForTab();
    }

    public int getActiveTabIndex() {
        return activeTabIndex;
    }

    public void setShopIdentifier(String shopIdentifier) {
        getTileEntity().setShop(shopIdentifier);
        setShopFromTile();
        sendShopToListeners(false);
    }

    private void sendShopToListeners(boolean update) {
        for (int j = 0; j < this.listeners.size(); ++j) {
            if (listeners.get(j) instanceof EntityPlayerMP) {
                PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerShop(getTileEntity().getShop(), update), (EntityPlayerMP) listeners.get(j));
            }
        }
    }
}
