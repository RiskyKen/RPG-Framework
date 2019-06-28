package moe.plushie.rpgeconomy.currency.common;

import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.api.currency.IItemMatcher;
import moe.plushie.rpgeconomy.api.currency.IWallet;

public class Cost implements ICost {

    private final IWallet walletCost;
    private final IItemMatcher[] itemCost;
    
    public Cost(IWallet walletCost, IItemMatcher[] itemCost) {
        this.walletCost = walletCost;
        this.itemCost = itemCost;
    }
    
    @Override
    public IWallet getWalletCost() {
        return walletCost;
    }

    @Override
    public IItemMatcher[] getItemCost() {
        return itemCost;
    }
    
    @Override
    public boolean hasWalletCost() {
        return walletCost != null;
    }
    
    @Override
    public boolean hasItemCost() {
        return itemCost != null;
    }
}
