package moe.plushie.rpgeconomy.api.currency;

import net.minecraft.item.ItemStack;

public interface ICurrency {
    
    /** Name of the currency. (this is used as the currency ID) */
    public String getName();
    
    /** Will a wallet item be generated for this currency. */
    public boolean getHasWallet();
    
    /** Must the player have the wallet item in their inventory to open the wallet GUI. */
    public boolean getNeedItemToOpen();
    
    /** Can the wallet GUI be opened with a key binding. */
    public boolean getOpensWithKeybind();
    
    /** Different variants of this currency. */
    public ICurrencyVariant[] getCurrencyVariants();
    
    public static interface ICurrencyVariant {
        
        public String getName();
        
        public int getValue();
        
        public ItemStack getItem();
    }
}
