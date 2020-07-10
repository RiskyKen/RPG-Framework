package moe.plushie.rpg_framework.currency.common;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.ICurrency.ICurrencyVariant;
import moe.plushie.rpg_framework.api.currency.ICurrencyCapability;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.core.common.utils.UtilItems;
import moe.plushie.rpg_framework.currency.common.capability.CurrencyCapability;
import moe.plushie.rpg_framework.currency.common.items.ItemWallet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
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
    
    public static int addAmountToWallet(ICurrency currency, EntityPlayer player, int amount) {
        if (currency.getCurrencyWalletInfo().getNeedItemToAccess() & !haveWalletForCurrency(player, currency)) {
            return amount;
        }
        ICurrencyCapability currencyCap = CurrencyCapability.get(player);
        if (currencyCap != null) {
            IWallet wallet = currencyCap.getWallet(currency);
            if (wallet != null) {
                wallet.addAmount(amount);
                return 0;
            }
        }
        return amount;
    }

    public static boolean addAmountToInventory(ICurrency currency, EntityPlayer player, int amount, boolean simulate) {
        if (simulate) {
            return true;
            //inventory = copyInventory(inventory);
        }

        for (int i = currency.getCurrencyVariants().length; i > 0; i--) {
            ICurrencyVariant variant = currency.getCurrencyVariants()[i - 1];
            while (variant.getValue() <= amount) {
                UtilItems.spawnItemAtEntity(player, variant.getItem().getItemStack().copy(), true);
                amount -= variant.getValue();
            }
        }

        if (amount == 0) {
            return true;
        }
        return false;
    }

    public static boolean consumeAmountFromInventory(ICurrency currency, InventoryPlayer inventoryPlayer, int amount, boolean simulate) {
        IInventory inv = inventoryPlayer;
        if (simulate) {
            inv = copyInventory(inventoryPlayer);
        }

        // Remove full coins.
        for (int j = currency.getCurrencyVariants().length; j > 0; j--) {
            ICurrencyVariant variant = currency.getCurrencyVariants()[j - 1];

            if (variant.getValue() <= amount) {
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack stack = inv.getStackInSlot(i);
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
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
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
            if (simulate) {
                return true;
            } else {
                // TODO Should try and add it to the wallet if we can.
                if (addAmountToInventory(currency, inventoryPlayer.player, -amount, simulate)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean payWithItems(InventoryPlayer inventoryPlayer, IItemMatcher[] itemCost, boolean simulate) {
        IInventory inv = inventoryPlayer;
        if (simulate) {
            inv = copyInventory(inventoryPlayer);
        }

        int[] neededAmounts = new int[itemCost.length];
        for (int i = 0; i < neededAmounts.length; i++) {
            if (!itemCost[i].getItemStack().isEmpty()) {
                neededAmounts[i] = itemCost[i].getItemStack().getCount();
            } else {
                neededAmounts[i] = 0;
            }
        }

        for (int i = 0; i < neededAmounts.length; i++) {
            for (int j = 0; j < inv.getSizeInventory(); j++) {
                ItemStack stack = inv.getStackInSlot(j);
                if (!stack.isEmpty()) {
                    if (itemCost[i].matches(stack)) {
                        int takeAmount = Math.min(stack.getCount(), neededAmounts[i]);
                        neededAmounts[i] -= takeAmount;
                        stack.shrink(takeAmount);
                    }
                }
            }
        }

        for (int i = 0; i < neededAmounts.length; i++) {
            if (neededAmounts[i] > 0) {
                return false;
            }
        }

        return true;
    }

    public static IInventory copyInventory(InventoryPlayer inventory) {
        IInventory copyInventory = new InventoryBasic("", false, inventory.getSizeInventory());
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            copyInventory.setInventorySlotContents(i, inventory.getStackInSlot(i).copy());
        }
        return copyInventory;
    }

    public static boolean haveWalletForCurrency(EntityPlayer player, ICurrency currency) {
        ItemStack stack = ItemWallet.getWallet(currency);
        if (!stack.isEmpty()) {
            return player.inventory.hasItemStack(stack);
        }
        return false;
    }
}
