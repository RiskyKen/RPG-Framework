package moe.plushie.rpgeconomy.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMailBox extends ModContainer {

    public ContainerMailBox(InventoryPlayer inventoryPlayer) {
        super(inventoryPlayer);
        int playerInvY = 142;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(inventoryPlayer, x, 8 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 8 + 18 * x, playerInvY + y * 18));
            }
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotId) {
        return null;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }
}
