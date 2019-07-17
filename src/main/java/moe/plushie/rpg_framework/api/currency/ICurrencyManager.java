package moe.plushie.rpg_framework.api.currency;

public interface ICurrencyManager {
    
    public ICurrency getCurrency(String identifier);
    
    public ICurrency[] getCurrencies();
    
    public String[] getCurrencyNames();
}
