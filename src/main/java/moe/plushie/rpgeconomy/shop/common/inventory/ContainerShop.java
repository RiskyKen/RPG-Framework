package moe.plushie.rpgeconomy.shop.common.inventory;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.api.shop.IShop.IShopItem;
import moe.plushie.rpgeconomy.api.shop.IShop.IShopTab;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.init.ModSounds;
import moe.plushie.rpgeconomy.core.common.inventory.ModTileContainer;
import moe.plushie.rpgeconomy.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerShop;
import moe.plushie.rpgeconomy.core.common.utils.UtilItems;
import moe.plushie.rpgeconomy.shop.common.Shop.ShopItem;
import moe.plushie.rpgeconomy.shop.common.Shop.ShopTab;
import moe.plushie.rpgeconomy.shop.common.ShopManager;
import moe.plushie.rpgeconomy.shop.common.inventory.slot.SlotShop;
import moe.plushie.rpgeconomy.shop.common.tileentities.TileEntityShop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ContainerShop extends ModTileContainer<TileEntityShop> {

    private final InventoryBasic invShop;
    private final InventoryBasic invPrice;
    private final ArrayList<Slot> slotsShop;
    private final ArrayList<Slot> slotsPrice;

    private IShop shop;
    private boolean editMode = false;
    private int activeTabIndex = 0;
    private boolean dirty = false;

    public ContainerShop(EntityPlayer entityPlayer, TileEntityShop tileEntity) {
        super(entityPlayer, tileEntity);
        invShop = new InventoryBasic("shop", false, 8);
        invPrice = new InventoryBasic("price", false, 5);
        slotsShop = new ArrayList<Slot>();
        slotsPrice = new ArrayList<Slot>();

        if (!entityPlayer.getEntityWorld().isRemote) {
            setShopFromTile();
        }

        for (int i = 0; i < 4; i++) {
            addSlotToContainerAndList(new SlotShop(invShop, i, 32, 25 + i * 31, this), slotsShop);
        }

        for (int i = 0; i < 4; i++) {
            addSlotToContainerAndList(new SlotShop(invShop, i + 4, 168, 25 + i * 31, this), slotsShop);
        }
        


        if (ConfigHandler.showPlayerInventoryInShopGUI) {
            addPlayerSlots(24, 162);
        }
        
        for (int i = 0; i < invPrice.getSizeInventory(); i++) {
            SlotHidable slotHidable = new SlotHidable(invPrice, i, 76 + i * 38, 33);
            addSlotToContainerAndList(slotHidable, slotsPrice);
            slotHidable.setVisible(true);
        }
    }

    public ArrayList<Slot> getSlotsShop() {
        return slotsShop;
    }

    public ArrayList<Slot> getSlotsPrice() {
        return slotsPrice;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        World world = player.getEntityWorld();
        if (!editMode & clickTypeIn == ClickType.PICKUP & !world.isRemote & shop != null & slotId >= 0 & slotId < 8) {
            ItemStack itemStack = inventorySlots.get(slotId).getStack();
            if (!itemStack.isEmpty()) {
                IShopItem shopItem = shop.getTabs().get(activeTabIndex).getItems().get(slotId);
                ICost cost = shopItem.getCost();
                if (cost.canAfford(player)) {
                    cost.pay(player);
                    world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.COIN_WITHDRAW, SoundCategory.PLAYERS, 0.3F, 0.8F + (player.getRNG().nextFloat() * 0.4F));
                    if (!player.inventory.addItemStackToInventory(shopItem.getItem().copy())) {
                        UtilItems.spawnItemAtEntity(player, shopItem.getItem().copy(), true);
                    }
                    detectAndSendChanges();
                }
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    private void setShopFromTile() {
        this.shop = tileEntity.getShop();
        changeTab(0);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote & dirty & shop != null) {
            saveShop();
        }
    }

    private void setSlotForTab() {
        for (int i = 0; i < invShop.getSizeInventory(); i++) {
            invShop.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        if (shop != null && shop.getTabCount() > 0) {
            if (activeTabIndex != -1) {
                for (int i = 0; i < shop.getTabs().get(activeTabIndex).getItemCount(); i++) {
                    if (i < 8) {
                        invShop.setInventorySlotContents(i, shop.getTabs().get(activeTabIndex).getItems().get(i).getItem());
                    }
                }
            }
        }
    }
    

    public void gotCostRequest(int slotIndex) {
        setSlotsForPrice(slotIndex);
    }
    
    private void setSlotsForPrice(int slotIndex) {
        //detectAndSendChanges();
        for (int i = 0; i < invPrice.getSizeInventory(); i++) {
            invPrice.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        //detectAndSendChanges();
        if (shop != null && shop.getTabCount() > 0) {
            if (activeTabIndex != -1) {
                IShopItem shopItem = shop.getTabs().get(activeTabIndex).getItems().get(slotIndex);
                if (!shopItem.getItem().isEmpty()) {
                    ICost cost = shopItem.getCost();
                    if (cost != null && cost.hasItemCost()) {
                        for (int i = 0; i < cost.getItemCost().length; i++) {
                            if (i < invPrice.getSizeInventory()) {
                                invPrice.setInventorySlotContents(i, cost.getItemCost()[i].getItemStack().copy());
                            }
                        }
                    }
                }
            }
        }
        //detectAndSendChanges();
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
        dirty = true;
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

    public void setShopIdentifier(IIdentifier identifier) {
        getTileEntity().setShop(identifier);
        setShopFromTile();
        sendShopToListeners(false);
    }

    private void sendShopToListeners(boolean update) {
        dirty = true;
        for (int j = 0; j < this.listeners.size(); ++j) {
            if (listeners.get(j) instanceof EntityPlayerMP) {
                PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerShop(shop, update), (EntityPlayerMP) listeners.get(j));
            }
        }
    }

    public void addShop(String shopName) {
        ShopManager shopManager = RpgEconomy.getProxy().getShopManager();
        shopManager.addShop(shopName);
        shopManager.syncToClient((EntityPlayerMP) getEntityPlayer());
    }

    public void removeShop(IIdentifier shopIdentifier) {
        ShopManager shopManager = RpgEconomy.getProxy().getShopManager();
        shopManager.removeShop(shopIdentifier);
        shopManager.syncToClient((EntityPlayerMP) getEntityPlayer());
    }
}
