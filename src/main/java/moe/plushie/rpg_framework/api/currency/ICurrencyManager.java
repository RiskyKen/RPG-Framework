package moe.plushie.rpg_framework.api.currency;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public interface ICurrencyManager {
    
    public ICurrency getCurrency(IIdentifier identifier);
    
    public ICurrency[] getCurrencies();
    
    public String[] getCurrencyNames();
}
