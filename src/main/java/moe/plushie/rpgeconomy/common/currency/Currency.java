package moe.plushie.rpgeconomy.common.currency;

import java.util.Arrays;

import moe.plushie.rpgeconomy.RpgEconomy;
import net.minecraft.item.ItemStack;

public class Currency {
    
    private final String name;
    private final boolean showInWallet;
    private final Variant[] variants;
    
    public Currency(String name, boolean showInWallet, Variant[] variants) {
        this.name = name;
        this.showInWallet = showInWallet;
        this.variants = variants;
        RpgEconomy.getLogger().info("Loaded: " + this.toString());
    }

    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "Currency [name=" + name + ", variants=" + Arrays.toString(variants) + "]";
    }



    public static class Variant {
        
        private final String name;
        private final int value;
        private final ItemStack[] items;
        
        public Variant(String name, int value, ItemStack[] items) {
            this.name = name;
            this.value = value;
            this.items = items;
        }

        @Override
        public String toString() {
            return "Variant [name=" + name + ", value=" + value + ", items=" + Arrays.toString(items) + "]";
        }
    }
}
