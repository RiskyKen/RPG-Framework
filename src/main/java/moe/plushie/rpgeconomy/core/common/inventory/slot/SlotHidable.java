package moe.plushie.rpgeconomy.core.common.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotHidable extends Slot {

    private boolean visible;
    private int xDisplayPositionNormal;
    private int yDisplayPositionNormal;
    
    public SlotHidable(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.visible = true;
        this.xDisplayPositionNormal = xDisplayPosition;
        this.yDisplayPositionNormal = yDisplayPosition;
    }
    
    public void setDisplayPosition(int x, int y) {
        this.xDisplayPositionNormal = x;
        this.yDisplayPositionNormal = y;
        if (visible) {
            this.xPos = x;
            this.yPos = y;
        }
    }

    public boolean isVisible() {
        return this.visible;
    }
    
    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return this.visible & super.canTakeStack(playerIn);
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return this.visible & super.isItemValid(stack);
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (this.visible) {
            this.xPos = xDisplayPositionNormal;
            this.yPos = yDisplayPositionNormal;
        } else {
            this.xPos = 100000;
            this.yPos = 100000;
        }
    }
}
