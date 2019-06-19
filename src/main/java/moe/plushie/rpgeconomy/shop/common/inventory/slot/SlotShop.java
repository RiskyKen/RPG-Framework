package moe.plushie.rpgeconomy.shop.common.inventory.slot;

import moe.plushie.rpgeconomy.core.common.inventory.slot.SlotHidable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotShop extends SlotHidable {

    public SlotShop(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }
    
    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }
}
