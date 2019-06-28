package moe.plushie.rpgeconomy.currency.common;

import moe.plushie.rpgeconomy.api.currency.ICurrency;
import moe.plushie.rpgeconomy.api.currency.ICurrency.ICurrencyVariant;
import moe.plushie.rpgeconomy.currency.common.items.ItemWallet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class CurrencyWalletHelper {

    private CurrencyWalletHelper() {
    }

    public static boolean haveAmountInInventory(ICurrency currency, InventoryPlayer inventory, int amount) {
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
                if (variant.getItem().matches(stack)) {
                    return variant.getValue() * stack.getCount();
                }
            }
        }
        return 0;
    }

    public static int consumeAllFromInventory(ICurrency currency, InventoryPlayer inventory) {
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

    public static boolean addAmountToInventory(ICurrency currency, InventoryPlayer inventory, int amount, boolean simulate) {
        if (simulate) {
            inventory = copyInventory(inventory);
        }

        for (int i = currency.getCurrencyVariants().length; i > 0; i--) {
            ICurrencyVariant variant = currency.getCurrencyVariants()[i - 1];
            while (variant.getValue() <= amount) {
                if (inventory.addItemStackToInventory(variant.getItem().getItemStack().copy())) {
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

    public static boolean consumeAmountFromInventory(ICurrency currency, InventoryPlayer inventory, int amount, boolean simulate) {
        if (simulate) {
            inventory = copyInventory(inventory);
        }

        // Remove full coins.
        for (int j = currency.getCurrencyVariants().length; j > 0; j--) {
            ICurrencyVariant variant = currency.getCurrencyVariants()[j - 1];

            if (variant.getValue() <= amount) {
                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (variant.getItem().matches(stack)) {
                        while (amount > 0 & !stack.isEmpty()) {
                            stack.shrink(1);
                            amount -= variant.getValue();
                        }
                    }
                    if (amount < 1) {
                        break;
                    }
                }
            }

            if (amount < 1) {
                break;
            }
        }

        if (amount == 0) {
            return true;
        }

        // Remove remainder.
        for (ICurrencyVariant variant : currency.getCurrencyVariants()) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (variant.getItem().matches(stack)) {
                    while (!stack.isEmpty() & amount > 0) {
                        stack.shrink(1);
                        amount -= variant.getValue();
                    }
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

    private static InventoryPlayer copyInventory(InventoryPlayer inventory) {
        InventoryPlayer inventoryPlayer = new InventoryPlayer(null);
        for (int i = 0; i < inventoryPlayer.getSizeInventory(); i++) {
            inventoryPlayer.setInventorySlotContents(i, inventory.getStackInSlot(i).copy());
        }
        return inventoryPlayer;
    }
    
    public static boolean haveWalletForCurrency(EntityPlayer player, ICurrency currency) {
        ItemStack stack = ItemWallet.getWallet(currency);
        if (!stack.isEmpty()) {
            return player.inventory.hasItemStack(stack);
        }
        return false;
    }
}
