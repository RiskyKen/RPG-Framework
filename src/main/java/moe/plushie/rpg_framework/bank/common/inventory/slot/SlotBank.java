package moe.plushie.rpg_framework.bank.common.inventory.slot;

import moe.plushie.rpg_framework.bank.common.inventory.ContainerBank;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotBank extends SlotHidable {

    private final ContainerBank parent;

    public SlotBank(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition, ContainerBank parent) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.parent = parent;
    }

    @Override
    public void onSlotChanged() {
        if (parent != null) {
            //parent.onSlotChanged(getSlotIndex(), getStack());
        }
        super.onSlotChanged();
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        if (parent.getBank() == null || parent.getBankAccount() == null || parent.getBankAccount().getTabCount() == 0) {
            return false;
        }
        if (parent.getActiveTabIndex() == -1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (parent.getBank() == null || parent.getBankAccount() == null || parent.getBankAccount().getTabCount() == 0) {
            return false;
        }
        if (parent.getActiveTabIndex() == -1) {
            return false;
        }
        return true;
    }
}