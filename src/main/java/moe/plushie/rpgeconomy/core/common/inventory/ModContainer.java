package moe.plushie.rpgeconomy.core.common.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import moe.plushie.rpgeconomy.core.common.inventory.slot.SlotHidable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ModContainer extends Container {

    protected final InventoryPlayer invPlayer;
    private final ArrayList<Slot> slotsPlayer;
    
    private int playerInvStartIndex;
    private int playerInvEndIndex;
    
    public ModContainer(InventoryPlayer invPlayer) {
        this.invPlayer = invPlayer;
        this.slotsPlayer = new ArrayList<Slot>();
    }
    
    protected void addSlotToContainerAndList(Slot slot, List<Slot> list) {
        addSlotToContainer(slot);
        list.add(slot);
    }
    
    protected void addPlayerSlots(int posX, int posY) {
        playerInvStartIndex = inventorySlots.size();
        int playerInvY = posY;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainerAndList(new SlotHidable(invPlayer, x, posX + 18 * x, hotBarY), slotsPlayer);
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainerAndList(new SlotHidable(invPlayer, x + y * 9 + 9, posX + 18 * x, playerInvY + y * 18), slotsPlayer);
            }
        }
        playerInvEndIndex  = inventorySlots.size();
    }
    
    public int getPlayerInvStartIndex() {
        return playerInvStartIndex;
    }
    
    public int getPlayerInvEndIndex() {
        return playerInvEndIndex;
    }
    
    public ArrayList<Slot> getSlotsPlayer() {
        return slotsPlayer;
    }
    
    public boolean isSlotPlayerInv(int index) {
        return index >= playerInvStartIndex & index < playerInvEndIndex;
    }
    
    protected boolean canSlotHoldItem(int slotIndex, ItemStack itemStack) {
        Slot slot = getSlot(slotIndex);
        return canSlotHoldItem(slot, itemStack);
    }
    
    protected boolean canSlotHoldItem(Slot slot, ItemStack itemStack) {
        return slot.isEnabled() & slot.isItemValid(itemStack);
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (!isSlotPlayerInv(index)) {
            Slot slot = getSlot(index);
            if (slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                ItemStack result = stack.copy();
                // Moving from tile entity to player.
                if (!this.mergeItemStack(stack, playerInvStartIndex + 9, playerInvEndIndex, false)) {
                    if (!this.mergeItemStack(stack, playerInvStartIndex, playerInvStartIndex + 9, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                if (stack.getCount() == 0) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }
                slot.onTake(playerIn, stack);
                return result;
            }
            return ItemStack.EMPTY;
        } else {
            return transferStackFromPlayer(playerIn, index);
        }
    }
    
    @Nonnull
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isDead;
    }
}
