package moe.plushie.rpgeconomy.currency.common;

import moe.plushie.rpgeconomy.api.currency.ICurrency;
import moe.plushie.rpgeconomy.api.currency.ICurrency.ICurrencyVariant;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
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

    public static boolean addAmountToInventory(ICurrency currency, IInventory inventory, int amount, boolean simulate) {
        if (simulate) {
            inventory = copyInventory(inventory);
        }
        for (int i = currency.getCurrencyVariants().length; i > 0; i--) {
            ICurrencyVariant variant = currency.getCurrencyVariants()[i - 1];
            while (variant.getValue() <= amount) {
                ItemStack stack = addItem(inventory, variant.getItem());
                if (stack.isEmpty()) {
                    amount -= variant.getValue();
                } else {
                    break;
                }
            }
        }
        if (amount == 0) {
            return true;
        }
        return false;
    }

    public static boolean consumeAmountFromInventory(ICurrency currency, IInventory inventory, int amount, boolean simulate) {
        if (simulate) {
            inventory = copyInventory(inventory);
        }
        for (ICurrencyVariant variant : currency.getCurrencyVariants()) {
            
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack.isItemEqual(variant.getItem())) {
                    amount -= variant.getValue() * stack.getCount();
                    inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
                if (amount < 1) {
                    break;
                }
            }
            if (amount < 1) {
                break;
            }
        }
        
        if (amount == 0) {
            return true;
        }
        if (amount < 0) {
            if (addAmountToInventory(currency, inventory, -amount, simulate)) {
                return true;
            }
        }
        return false;
    }

    private static IInventory copyInventory(IInventory inventory) {
        InventoryBasic inventoryBasic = new InventoryBasic(inventory.getName(), false, inventory.getSizeInventory());
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            inventoryBasic.setInventorySlotContents(i, inventory.getStackInSlot(i).copy());
        }
        return inventoryBasic;
    }

    private static ItemStack addItem(IInventory inventory, ItemStack stack) {
        ItemStack itemstack = stack.copy();

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack itemstack1 = inventory.getStackInSlot(i);

            if (itemstack1.isEmpty()) {
                inventory.setInventorySlotContents(i, itemstack);
                inventory.markDirty();
                return ItemStack.EMPTY;
            }

            if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
                int j = Math.min(inventory.getInventoryStackLimit(), itemstack1.getMaxStackSize());
                int k = Math.min(itemstack.getCount(), j - itemstack1.getCount());

                if (k > 0) {
                    itemstack1.grow(k);
                    itemstack.shrink(k);

                    if (itemstack.isEmpty()) {
                        inventory.markDirty();
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (itemstack.getCount() != stack.getCount()) {
            inventory.markDirty();
        }

        return itemstack;
    }
}
