package moe.plushie.rpg_framework.api.currency;

public interface IWallet {
    
    public ICurrency getCurrency();
    
    public void setAmount(int amount);
    
    public int getAmount();
    
    public void addAmount(int amount);
    
    public void removeAmount(int amount);
}
