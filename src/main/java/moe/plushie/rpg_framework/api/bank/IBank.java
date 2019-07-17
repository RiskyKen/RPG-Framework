package moe.plushie.rpg_framework.api.bank;

import moe.plushie.rpg_framework.api.currency.ICost;

public interface IBank {

    public String getIdentifier();

    /** Name of the bank. */
    public String getName();

    public ICost getDepositCost();

    public ICost getWithdrawCost();
    
    public int getTabSlotCountWidth();
    
    public int getTabSlotCountHeight();
    
    public int getTabSlotCount();

    public int getTabStartingCount();

    public int getTabMaxCount();
    
    public int getTabUnlockableCount();

    public ICost getTabUnlockCost(int index);
}
