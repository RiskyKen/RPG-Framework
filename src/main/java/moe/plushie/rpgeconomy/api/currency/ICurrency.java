package moe.plushie.rpgeconomy.api.currency;

import net.minecraft.item.ItemStack;

public interface ICurrency {

    public String getName();
    
    public boolean getHasWallet();
    
    public boolean getNeedItemToOpen();
    
    public boolean getOpensWithKeybind();
    
    public ICurrencyVariant[] getVariants();
    
    public static interface ICurrencyVariant {
        
        public String getName();
        
        public int getValue();
        
        public ItemStack getItem();
    }
}
