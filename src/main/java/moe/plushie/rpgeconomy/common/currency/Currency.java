package moe.plushie.rpgeconomy.common.currency;

import java.util.Arrays;

import net.minecraft.item.ItemStack;

public class Currency {
    
    /** Name of the currency. (this is used as the currency ID) */
    private final String name;
    
    /** Will a wallet item be generated for this currency. */
    private final boolean hasWallet;
    
    /** Must the player have the wallet item in their inventory to open the wallet GUI. */
    private final boolean needItemToOpen;
    
    /** Can the wallet GUI be opened with a key binding. */
    private final boolean opensWithKeybind;
    
    /** Different variants of this currency. */
    private final Variant[] variants;
    
    public Currency(String name, boolean hasWallet, boolean needItemToOpen, boolean opensWithKeybind, Variant[] variants) {
        this.name = name;
        this.hasWallet = hasWallet;
        this.needItemToOpen = needItemToOpen;
        this.opensWithKeybind = opensWithKeybind;
        this.variants = variants;
    }

    public String getName() {
        return name;
    }
    
    public boolean getHasWallet() {
        return hasWallet;
    }
    
    public boolean getNeedItemToOpen() {
        return needItemToOpen;
    }
    
    public boolean getOpensWithKeybind() {
        return opensWithKeybind;
    }
    
    public Variant[] getVariants() {
        return variants;
    }
    
    @Override
    public String toString() {
        return "Currency [name=" + name + ", hasWallet=" + hasWallet + ", needItemToOpen=" + needItemToOpen + ", opensWithKeybind=" + opensWithKeybind + ", variants=" + Arrays.toString(variants) + "]";
    }

    public static class Variant {
        
        private final String name;
        private final int value;
        private final ItemStack item;
        
        public Variant(String name, int value, ItemStack item) {
            this.name = name;
            this.value = value;
            this.item = item;
        }
        
        public String getName() {
            return name;
        }
        
        public int getValue() {
            return value;
        }
        
        public ItemStack getItem() {
            return item;
        }

        @Override
        public String toString() {
            return "Variant [name=" + name + ", value=" + value + ", item=" + item + "]";
        }
    }
}
