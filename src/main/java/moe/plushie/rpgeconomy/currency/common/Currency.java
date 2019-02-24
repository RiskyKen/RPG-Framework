package moe.plushie.rpgeconomy.currency.common;

import java.util.Arrays;

import moe.plushie.rpgeconomy.api.currency.ICurrency;
import net.minecraft.item.ItemStack;

public class Currency implements ICurrency {

    /** Name of the currency. (this is used as the currency ID) */
    private final String name;

    /** Will a wallet item be generated for this currency. */
    private final boolean hasWallet;

    /**
     * Must the player have the wallet item in their inventory to open the wallet
     * GUI.
     */
    private final boolean needItemToOpen;

    /** Can the wallet GUI be opened with a key binding. */
    private final boolean opensWithKeybind;

    /** Different variants of this currency. */
    private final CurrencyVariant[] variants;

    public Currency(String name, boolean hasWallet, boolean needItemToOpen, boolean opensWithKeybind, CurrencyVariant[] variants) {
        this.name = name;
        this.hasWallet = hasWallet;
        this.needItemToOpen = needItemToOpen;
        this.opensWithKeybind = opensWithKeybind;
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
    public boolean getNeedItemToOpen() {
        return needItemToOpen;
    }

    @Override
    public boolean getOpensWithKeybind() {
        return opensWithKeybind;
    }

    @Override
    public CurrencyVariant[] getCurrencyVariants() {
        return variants;
    }

    @Override
    public String toString() {
        return "Currency [name=" + name + ", hasWallet=" + hasWallet + ", needItemToOpen=" + needItemToOpen + ", opensWithKeybind=" + opensWithKeybind + ", variants=" + Arrays.toString(variants) + "]";
    }

    public static class CurrencyVariant implements ICurrencyVariant {

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
    }
}
