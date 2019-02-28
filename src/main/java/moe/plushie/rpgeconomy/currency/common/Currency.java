package moe.plushie.rpgeconomy.currency.common;

import java.util.Arrays;

import moe.plushie.rpgeconomy.api.currency.ICurrency;
import net.minecraft.item.ItemStack;

public class Currency implements ICurrency {

    /** Name of the currency. (this is used as the currency ID) */
    private final String name;

    /** Will a wallet item be generated for this currency. */
    private final boolean hasWallet;

    /** Must the player have the wallet item in their inventory to access the wallet GUI. */
    private final boolean needItemToAccess;

    /** Can the wallet GUI be opened with a key binding. */
    private final boolean opensWithKeybind;

    /** Should picked up items be auto added to the wallet. */
    private final boolean pickupIntoWallet;

    private final String displayFormat;

    /** Different variants of this currency. */
    private final CurrencyVariant[] variants;

    public Currency(String name, boolean hasWallet, boolean needItemToAccess, boolean opensWithKeybind, boolean pickupIntoWallet, String displayFormat, CurrencyVariant[] variants) {
        this.name = name;
        this.hasWallet = hasWallet;
        this.needItemToAccess = needItemToAccess;
        this.opensWithKeybind = opensWithKeybind;
        this.pickupIntoWallet = pickupIntoWallet;
        this.displayFormat = displayFormat;
        this.variants = variants;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean getHasWallet() {
        return hasWallet;
    }

    @Override
    public boolean getNeedItemToAccess() {
        return needItemToAccess;
    }

    @Override
    public boolean getOpensWithKeybind() {
        return opensWithKeybind;
    }

    public boolean getPickupIntoWallet() {
        return pickupIntoWallet;
    }

    @Override
    public CurrencyVariant[] getCurrencyVariants() {
        return variants;
    }

    @Override
    public String getDisplayFormat() {
        return displayFormat;
    }

    @Override
    public String toString() {
        return "Currency [name=" + name + ", hasWallet=" + hasWallet + ", needItemToAccess=" + needItemToAccess + ", opensWithKeybind=" + opensWithKeybind + ", pickupIntoWallet=" + pickupIntoWallet + ", displayFormat=" + displayFormat + ", variants=" + Arrays.toString(variants) + "]";
    }

    public static class CurrencyVariant implements ICurrencyVariant, Comparable<CurrencyVariant> {

        private final String name;
        private final int value;
        private final ItemStack item;

        public CurrencyVariant(String name, int value, ItemStack item) {
            this.name = name;
            this.value = value;
            this.item = item;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public ItemStack getItem() {
            return item;
        }

        @Override
        public String toString() {
            return "Variant [name=" + name + ", value=" + value + ", item=" + item + "]";
        }

        @Override
        public int compareTo(CurrencyVariant o) {
            return value - o.value;
        }
    }
}
