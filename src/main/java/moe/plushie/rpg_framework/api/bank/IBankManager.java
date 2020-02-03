package moe.plushie.rpg_framework.api.bank;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public interface IBankManager {
    
    public IBank getBank(IIdentifier identifier);
    
    public IBank[] getBanks();
    
    public String[] getBankNames();
}
