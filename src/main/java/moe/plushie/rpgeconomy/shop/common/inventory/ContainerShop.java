package moe.plushie.rpgeconomy.shop.common.inventory;

import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.inventory.ModTileContainer;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerShop;
import moe.plushie.rpgeconomy.shop.common.inventory.slot.SlotShop;
import moe.plushie.rpgeconomy.shop.common.tileentities.TileEntityShop;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
            this.shop = tileEntity.getShop();
            if (shop != null) {
                for (int i = 0; i < shop.getTabs()[activeTabIndex].getItemCount(); i++) {
                    if (i < 8) {
                        inventory.setInventorySlotContents(i, shop.getTabs()[activeTabIndex].getItems()[i].getItem());
                    }
                }
            }
        }
        
        if (ConfigHandler.showPlayerInventoryInShopGUI) {
            addPlayerSlots(29, 162);
        }

        for (int i = 0; i < 4; i++) {
            addSlotToContainer(new SlotShop(inventory, i, 32, 25 + i * 31));
            addSlotToContainer(new SlotShop(inventory, i + 4, 168, 25 + i * 31));
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
    }
    
    public int getActiveTabIndex() {
        return activeTabIndex;
    }
}
