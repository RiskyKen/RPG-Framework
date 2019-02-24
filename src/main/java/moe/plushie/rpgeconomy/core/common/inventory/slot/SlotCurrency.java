package moe.plushie.rpgeconomy.core.common.inventory.slot;

import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.Currency.CurrencyVariant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCurrency extends Slot {

    private final Currency currency;
    private final CurrencyVariant variant;

    public SlotCurrency(Currency currency, CurrencyVariant variant, IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.currency = currency;
        this.variant = variant;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.isItemEqualIgnoreDurability(variant.getItem());
    }
    
    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }
}
