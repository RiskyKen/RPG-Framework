package moe.plushie.rpgeconomy.api.currency;

public interface ICurrencyManager {
    
    public void reload(boolean syncWithClients);
    
    public ICurrency getCurrency(String name);
    
    public ICurrency[] getCurrencies();
}
