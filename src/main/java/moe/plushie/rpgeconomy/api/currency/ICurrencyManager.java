package moe.plushie.rpgeconomy.api.currency;

public interface ICurrencyManager {
    
    public ICurrency getCurrency(String identifier);
    
    public ICurrency[] getCurrencies();
    
    public String[] getCurrencyNames();
}
