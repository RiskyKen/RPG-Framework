package moe.plushie.rpg_framework.shop.common.inventory;

import java.util.ArrayList;

import com.google.common.util.concurrent.FutureCallback;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.api.shop.IShop;
import moe.plushie.rpg_framework.api.shop.IShop.IShopItem;
import moe.plushie.rpg_framework.api.shop.IShop.IShopTab;
import moe.plushie.rpg_framework.api.shop.IShop.IShopTab.TabType;
import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.init.ModSounds;
import moe.plushie.rpg_framework.core.common.inventory.ModContainer;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerShop;
import moe.plushie.rpg_framework.core.common.utils.PlayerUtils;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.Cost.CostFactory;
import moe.plushie.rpg_framework.itemData.ItemDataProvider;
import moe.plushie.rpg_framework.shop.ModuleShop;
import moe.plushie.rpg_framework.shop.common.Shop.ShopItem;
import moe.plushie.rpg_framework.shop.common.Shop.ShopTab;
import moe.plushie.rpg_framework.shop.common.ShopManager;
import moe.plushie.rpg_framework.shop.common.inventory.slot.SlotShop;
import moe.plushie.rpg_framework.shop.common.tileentities.TileEntityShop;
import moe.plushie.rpg_framework.stats.common.database.TableStatsShopSales;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerShop extends ModContainer {

    private final InventoryBasic invShop;
    private final InventoryBasic invPrice;
    private final ArrayList<Slot> slotsShop;
    private final ArrayList<Slot> slotsPrice;

    private final EntityPlayer player;
    private IShop shop;
    private TileEntityShop tileEntity;
    private boolean editMode = false;
    private int activeTabIndex = -1;
    private boolean dirty = false;
    private boolean loadingShop = true;
    private boolean loadingShopLast = true;

    public ContainerShop(EntityPlayer entityPlayer, IIdentifier identifier, TileEntityShop tileEntityShop) {
        super(entityPlayer.inventory);
        this.player = entityPlayer;
        // this.shop = shop;
        this.tileEntity = tileEntityShop;
        invShop = new InventoryBasic("shop", false, 8);
        invPrice = new InventoryBasic("price", false, 5);
        slotsShop = new ArrayList<Slot>();
        slotsPrice = new ArrayList<Slot>();

        if (!entityPlayer.getEntityWorld().isRemote) {
            changeTab(-1);
        }

        for (int i = 0; i < 4; i++) {
            addSlotToContainerAndList(new SlotShop(invShop, i, 32, 25 + i * 31, this), slotsShop);
        }

        for (int i = 0; i < 4; i++) {
            addSlotToContainerAndList(new SlotShop(invShop, i + 4, 168, 25 + i * 31, this), slotsShop);
        }

        if (ConfigHandler.optionsShared.showPlayerInventoryInShopGUI) {
            addPlayerSlots(24, 162);
        }

        for (int i = 0; i < invPrice.getSizeInventory(); i++) {
            SlotHidable slotHidable = new SlotHidable(invPrice, i, 76 + i * 38, 33);
            addSlotToContainerAndList(slotHidable, slotsPrice);
            slotHidable.setVisible(true);
        }
        // sendShopToListeners(false);
        loadShop(identifier);
    }

    public ArrayList<Slot> getSlotsShop() {
        return slotsShop;
    }

    public ArrayList<Slot> getSlotsPrice() {
        return slotsPrice;
    }

    private void loadShop(IIdentifier identifier) {
        if (isRemote()) {
            return;
        }
        loadingShop = true;
        if (identifier == null) {
            setShop(null);
            return;
        }
        ModuleShop.getShopManager().getShopAsync(identifier, new FutureCallback<IShop>() {

            @Override
            public void onSuccess(IShop result) {
                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        setShop(result);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        World world = player.getEntityWorld();

        if (!editMode & clickTypeIn == ClickType.PICKUP & !world.isRemote & shop != null & activeTabIndex != -1) {
            IShopTab activeTab = shop.getTabs().get(getActiveTabIndex());

            // Buying item.
            if (activeTab.getTabType() == TabType.BUY & slotId >= 0 & slotId < 8) {
                itemBuy(world, player, activeTab, slotId);
                return ItemStack.EMPTY;
            }

            if (activeTab.getTabType() == TabType.SELL) {
                // Selling item.
                if (slotId >= getPlayerInvStartIndex() & slotId < getPlayerInvEndIndex()) {
                    itemSell(world, player, activeTab, slotId);
                    return ItemStack.EMPTY;
                }

                // Buyback item.
                if (slotId >= 0 & slotId < 8) {
                    itemBuyback(world, player, activeTab, slotId);
                    return ItemStack.EMPTY;
                }
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    private void itemBuy(World world, EntityPlayer player, IShopTab activeTab, int slotId) {
        ItemStack itemStack = inventorySlots.get(slotId).getStack();
        if (!itemStack.isEmpty()) {
            IShopItem shopItem = activeTab.getItems().get(slotId);
            ICost cost = shopItem.getCost();
            if (cost.canAfford(player)) {
                DatabaseManager.createTaskAndExecute(new Runnable() {

                    @Override
                    public void run() {
                        TableStatsShopSales.updateSoldItemCount(shop.getIdentifier(), itemStack);
                    }
                });
                cost.pay(player);
                world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.COIN_WITHDRAW, SoundCategory.PLAYERS, 0.3F, 0.8F + (player.getRNG().nextFloat() * 0.4F));
                PlayerUtils.giveItemToPlayer(player, shopItem.getItem());
                detectAndSendChanges();
            }
        }
    }

    private void itemSell(World world, EntityPlayer player, IShopTab activeTab, int slotId) {
        ItemStack itemStack = inventorySlots.get(slotId).getStack();
        if (!itemStack.isEmpty()) {
            IItemData itemData = ItemDataProvider.getItemData(itemStack);
            // RPGFramework.getLogger().info("Selling item, slot id: " + activeTab.getItems().size());
            if (itemData.getValue().hasWalletCost()) {

                CostFactory costFactory = CostFactory.newCost();
                for (int i = 0; i < itemStack.getCount(); i++) {
                    costFactory.addCost(itemData.getValue());
                }
                ICost cost = costFactory.build();
                cost.refund(player);
                // player.getEntityWorld().playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, ModSounds.COIN_DEPOSIT, SoundCategory.PLAYERS, 0.5F, 0.8F + (player.getRNG().nextFloat() * 0.4F));
                activeTab.getItems().add(0, new ShopItem(itemStack.copy(), cost));
                activeTab.getItems().remove(activeTab.getItems().size() - 1);
                PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerShop(shop, true), (EntityPlayerMP) player);
                itemStack.setCount(0);
                // sendShopToListeners(true);
                // detectAndSendChanges();
            }
        }
    }

    private void itemBuyback(World world, EntityPlayer player, IShopTab activeTab, int slotId) {
        IShopItem shopItem = activeTab.getItems().get(slotId);
        ICost cost = shopItem.getCost();
        ItemStack itemStack = shopItem.getItem();
        // RPGFramework.getLogger().info("Buyback item, slot id: " + slotId);
        if (!itemStack.isEmpty()) {
            if (cost.canAfford(player)) {
                activeTab.getItems().remove(slotId);
                activeTab.getItems().add(new ShopItem(ItemStack.EMPTY, Cost.NO_COST));
                cost.pay(player);
                world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.COIN_WITHDRAW, SoundCategory.PLAYERS, 0.3F, 0.8F + (player.getRNG().nextFloat() * 0.4F));
                PlayerUtils.giveItemToPlayer(player, itemStack);
                PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerShop(shop, true), (EntityPlayerMP) player);
            }
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icontainerlistener = this.listeners.get(i);
            if (loadingShop != loadingShopLast) {
                if (loadingShop) {
                    icontainerlistener.sendWindowProperty(this, 0, 1);
                } else {
                    icontainerlistener.sendWindowProperty(this, 0, 0);
                }
            }
        }
        loadingShopLast = loadingShop;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            loadingShop = data == 1;
        }
    }

    public boolean isLoadingShop() {
        return loadingShop;
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
        // detectAndSendChanges();
        for (int i = 0; i < invPrice.getSizeInventory(); i++) {
            invPrice.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        // detectAndSendChanges();
        if (shop != null && shop.getTabCount() > 0) {
            if (activeTabIndex != -1) {
                IShopItem shopItem = shop.getTabs().get(activeTabIndex).getItems().get(slotIndex);
                if (!shopItem.getItem().isEmpty()) {
                    ICost cost = shopItem.getCost();
                    if (cost != null && cost.hasItemCost()) {
                        for (int i = 0; i < cost.getItemCosts().length; i++) {
                            if (i < invPrice.getSizeInventory()) {
                                invPrice.setInventorySlotContents(i, cost.getItemCosts()[i].getItemStack().copy());
                            }
                        }
                    }
                }
            }
        }
        // detectAndSendChanges();
    }

    public IShop getShop() {
        return shop;
    }

    public void setShop(IShop shop) {
        this.shop = shop;
        if (!player.getEntityWorld().isRemote) {
            changeTab(0);
            sendShopToListeners(false);
            loadingShop = false;
            // PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerShop(shop, false), (EntityPlayerMP) player);
        }
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

    public void tabAdd(int iconIndex, String tabName, TabType tabType, float valuePercentage) {
        shop.getTabs().add(new ShopTab(tabName, iconIndex, tabType, valuePercentage));
        sendShopToListeners(true);
    }

    public void tabEdit(int iconIndex, String tabName, TabType tabType, float valuePercentage) {
        IShopTab tabOld = shop.getTabs().get(activeTabIndex);
        shop.getTabs().set(activeTabIndex, new ShopTab(tabName, iconIndex, tabType, tabOld.getItems(), valuePercentage));
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
        ModuleShop.getShopManager().saveShopAsync(shop, null);
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
        if (editMode) {
            dirty = true;
        }
        this.editMode = editMode;
    }

    public void changeTab(int index) {
        if (shop != null) {
            if (shop.getTabCount() > 0) {
                activeTabIndex = MathHelper.clamp(index, 0, shop.getTabCount() - 1);
            } else {
                activeTabIndex = -1;
            }
        } else {
            activeTabIndex = -1;
        }
        if (!player.getEntityWorld().isRemote) {
            setSlotForTab();
        }
    }

    public int getActiveTabIndex() {
        return activeTabIndex;
    }

    public void setShopIdentifier(IIdentifier identifier) {
        if (tileEntity != null) {
            tileEntity.setShop(identifier);
            shop = null;
            changeTab(0);
        }
        sendShopToListeners(false);
        loadShop(identifier);
    }

    private void sendShopToListeners(boolean update) {
        for (int j = 0; j < this.listeners.size(); ++j) {
            if (listeners.get(j) instanceof EntityPlayerMP) {
                PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerShop(shop, update), (EntityPlayerMP) listeners.get(j));
            }
        }
    }

    public void addShop(String shopName) {
        ShopManager shopManager = ModuleShop.getShopManager();
        shopManager.createShopAsync(shopName, new FutureCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                shopManager.sendShopListToClient((EntityPlayerMP) player);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void removeShop(IIdentifier shopIdentifier) {
        ShopManager shopManager = ModuleShop.getShopManager();
        shopManager.removeShopAsync(shopIdentifier, new FutureCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                shopManager.sendShopListToClient((EntityPlayerMP) player);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
