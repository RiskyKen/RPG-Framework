package moe.plushie.rpgeconomy.api.currency;

public interface ICurrencyManager {
    
    public ICurrency getCurrency(String name);
    
    public ICurrency[] getCurrencies();
}
