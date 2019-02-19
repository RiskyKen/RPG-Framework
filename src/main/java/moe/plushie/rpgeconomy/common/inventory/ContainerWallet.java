package moe.plushie.rpgeconomy.common.inventory;

import moe.plushie.rpgeconomy.common.inventory.ModInventory.IInventoryCallback;
import moe.plushie.rpgeconomy.common.inventory.slot.SlotCurrency;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerWallet extends ModContainer implements IInventoryCallback {

    private final ModInventory inventoryWallet;
    
    public ContainerWallet(EntityPlayer entityPlayer) {
        super(entityPlayer.inventory);
        inventoryWallet = new ModInventory("wallet", 6, this);
        
        addPlayerSlots(8, 86);
        
        for (int i = 0; i < inventoryWallet.getSizeInventory(); i++) {
            addSlotToContainer(new SlotCurrency(null, null, inventoryWallet, i, 34 + i * 18, 40));
        }
    }

    @Override
    public void setInventorySlotContents(IInventory inventory, int index, ItemStack stack) {
        
    }

    @Override
    public void dirty() {
        
    }
}
