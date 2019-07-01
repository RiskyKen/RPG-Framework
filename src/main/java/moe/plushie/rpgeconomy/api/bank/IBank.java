package moe.plushie.rpgeconomy.api.bank;

import moe.plushie.rpgeconomy.api.currency.ICost;

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

    public ICost getTabUnlockCost(int index);
}
