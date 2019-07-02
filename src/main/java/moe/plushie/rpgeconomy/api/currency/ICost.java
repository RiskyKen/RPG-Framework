package moe.plushie.rpgeconomy.api.currency;

import moe.plushie.rpgeconomy.api.core.IItemMatcher;

public interface ICost {
    
    public IWallet getWalletCost();
    
    public IItemMatcher[] getItemCost();
    
    public boolean hasWalletCost();
    
    public boolean hasItemCost();
}
