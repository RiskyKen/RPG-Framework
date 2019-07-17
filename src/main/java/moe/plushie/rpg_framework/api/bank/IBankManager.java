package moe.plushie.rpg_framework.api.bank;

public interface IBankManager {
    
    public IBank getBank(String identifier);
    
    public IBank[] getBanks();
    
    public String[] getBankNames();
}
