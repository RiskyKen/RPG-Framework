package moe.plushie.rpgeconomy.common.inventory.slot;

import moe.plushie.rpgeconomy.common.currency.Currency;
import moe.plushie.rpgeconomy.common.currency.Currency.Variant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCurrency extends Slot {

    private final Currency currency;
    private final Variant variant;

    public SlotCurrency(Currency currency, Variant variant, IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.currency = currency;
        this.variant = variant;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.isItemEqualIgnoreDurability(variant.getItem());
    }
    
    @Override
    public ItemStack getStack() {
        super.getStack();
        // TODO Auto-generated method stub
        return variant.getItem().copy();
    }
    
    @Override
    public ItemStack decrStackSize(int amount) {
        super.decrStackSize(amount);
        // TODO Auto-generated method stub
        return variant.getItem().copy();
    }
    
    @Override
    public void putStack(ItemStack stack) {
        // TODO Auto-generated method stub
        super.putStack(stack);
    }
    
    @Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
        // TODO Auto-generated method stub
        return super.onTake(thePlayer, stack);
    }
    
    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
        // TODO Auto-generated method stub
        super.onSlotChange(p_75220_1_, p_75220_2_);
    }
    
}
