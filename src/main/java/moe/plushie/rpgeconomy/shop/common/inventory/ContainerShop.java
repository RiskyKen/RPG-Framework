package moe.plushie.rpgeconomy.shop.common.inventory;

import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.inventory.ModTileContainer;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerShop;
import moe.plushie.rpgeconomy.shop.common.inventory.slot.SlotShop;
import moe.plushie.rpgeconomy.shop.common.tileentities.TileEntityShop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

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
            addSlotToContainer(new SlotShop(inventory, i, 32, 25 + i * 31));
        }

        for (int i = 0; i < 4; i++) {
            addSlotToContainer(new SlotShop(inventory, i + 4, 168, 25 + i * 31));
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
                for (int i = 0; i < shop.getTabs()[activeTabIndex].getItemCount(); i++) {
                    if (i < 8) {
                        inventory.setInventorySlotContents(i, shop.getTabs()[activeTabIndex].getItems()[i].getItem());
                    }
                }
            }
        }
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void changeTab(int index) {
        activeTabIndex = index;
        setSlotForTab();
    }

    public int getActiveTabIndex() {
        return activeTabIndex;
    }

    public void setShopIdentifier(String shopIdentifier) {
        getTileEntity().setShop(shopIdentifier);
        setShopFromTile();
        for (int j = 0; j < this.listeners.size(); ++j) {
            if (listeners.get(j) instanceof EntityPlayerMP) {
                PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerShop(getTileEntity().getShop()), (EntityPlayerMP) listeners.get(j));
            }
        }
    }
}
