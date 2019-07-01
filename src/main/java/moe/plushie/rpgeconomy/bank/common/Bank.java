package moe.plushie.rpgeconomy.bank.common;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.currency.common.Cost;

public class Bank implements IBank {

    private final String identifier;
    private final String name;
    private final ICost depositCost;
    private final ICost withdrawCost;
    private final int tabSlotCountWidth;
    private final int tabSlotCountHeight;
    private final int tabStartingCount;
    private final int tabMaxCount;
    private final ICost[] tabUnlockCosts;

    public Bank(String identifier, String name, ICost depositCost, ICost withdrawCost, int tabSlotCountWidth, int tabSlotCountHeight, int tabStartingCount, int tabMaxCount, ICost[] tabUnlockCosts) {
        this.identifier = identifier;
        this.name = name;
        this.depositCost = depositCost;
        this.withdrawCost = withdrawCost;
        this.tabSlotCountWidth = tabSlotCountWidth;
        this.tabSlotCountHeight = tabSlotCountHeight;
        this.tabStartingCount = tabStartingCount;
        this.tabMaxCount = tabMaxCount;
        this.tabUnlockCosts = tabUnlockCosts;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ICost getDepositCost() {
        return depositCost;
    }

    @Override
    public ICost getWithdrawCost() {
        return withdrawCost;
    }

    @Override
    public int getTabSlotCountWidth() {
        return tabSlotCountWidth;
    }

    @Override
    public int getTabSlotCountHeight() {
        return tabSlotCountHeight;
    }
    
    @Override
    public int getTabSlotCount() {
        return tabSlotCountWidth * tabSlotCountHeight;
    }

    @Override
    public int getTabStartingCount() {
        return tabStartingCount;
    }

    @Override
    public int getTabMaxCount() {
        return tabMaxCount;
    }
    
    @Override
    public int getTabUnlockableCount() {
        return getTabMaxCount() - getTabStartingCount();
    }

    @Override
    public ICost getTabUnlockCost(int index) {
        if (index >= 0 & index < tabUnlockCosts.length) {
            return tabUnlockCosts[index];
        }
        return Cost.NO_COST;
    }
}
