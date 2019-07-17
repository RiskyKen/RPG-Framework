package moe.plushie.rpg_framework.shop.common.inventory.slot;

import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.shop.common.inventory.ContainerShop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotShop extends SlotHidable {

    private final ContainerShop parent;
    
    public SlotShop(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition, ContainerShop parent) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.parent = parent;
    }
    
    @Override
    public void onSlotChanged() {
        if (parent != null) {
            parent.onSlotChanged(getSlotIndex(), getStack());
        }
        super.onSlotChanged();
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        if (parent.getShop() == null || parent.getShop().getTabCount() == 0) {
            return false;
        }
        if (parent.getActiveTabIndex() == -1) {
            return false;
        }
        return parent.isEditMode();
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (parent.getShop() == null || parent.getShop().getTabCount() == 0) {
            return false;
        }
        if (parent.getActiveTabIndex() == -1) {
            return false;
        }
        return parent.isEditMode();
    }
}
