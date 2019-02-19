package moe.plushie.rpgeconomy.common.inventory.slot;

import moe.plushie.rpgeconomy.common.currency.Currency;
import moe.plushie.rpgeconomy.common.currency.Currency.Variant;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotCurrency extends Slot {

    private final Currency currency;
    private final Variant variant;

    public SlotCurrency(Currency currency, Variant variant, IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.currency = currency;
        this.variant = variant;
    }
}
