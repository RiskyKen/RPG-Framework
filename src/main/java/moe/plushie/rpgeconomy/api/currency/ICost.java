package moe.plushie.rpgeconomy.api.currency;

public interface ICost {
    
    public IWallet getWalletCost();
    
    public IItemMatcher[] getItemCost();
    
    public boolean hasWalletCost();
    
    public boolean hasItemCost();
}
