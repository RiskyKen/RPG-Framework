package moe.plushie.rpgeconomy.currency.common;

import moe.plushie.rpgeconomy.api.currency.ICurrency;
import moe.plushie.rpgeconomy.api.currency.ICurrency.ICurrencyVariant;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class CurrencyHelper {

    private CurrencyHelper() {
    }
    
    public static boolean haveAmountInInventory(ICurrency currency, IInventory inventory, int amount) {
        return getAmountInInventory(currency, inventory) >= amount;
    }
    
    public static int getAmountInInventory(ICurrency currency, IInventory inventory) {
        int value = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            value += getItemCurrencyValue(currency, stack);
        }
        return value;
    }
    
    public static int getItemCurrencyValue(ICurrency currency, ItemStack stack) {
        if (!stack.isEmpty()) {
            for (ICurrencyVariant variant : currency.getCurrencyVariants()) {
                if (stack.isItemEqual(variant.getItem())) {
                    return variant.getValue() * stack.getCount();
                }
            }
        }
        return 0;
    }
    
    public static int consumeAllFromInventory(ICurrency currency, IInventory inventory) {
        int value = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            int itemValue = getItemCurrencyValue(currency, stack);
            if (itemValue > 0) {
                value += itemValue;
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
        return value;
    }

    public static boolean consumeAmountFromInventory(ICurrency currency, IInventory inventory, int amount, boolean simulate) {
        for (ICurrencyVariant variant : currency.getCurrencyVariants()) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                
            }
        }
        return false;
    }
}
