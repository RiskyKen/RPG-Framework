package moe.plushie.rpg_framework.api.bank;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;

public interface IBank {

    public IIdentifier getIdentifier();

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
    
    public int getTabIconIndex();

    public ICost getTabUnlockCost(int index);
}
