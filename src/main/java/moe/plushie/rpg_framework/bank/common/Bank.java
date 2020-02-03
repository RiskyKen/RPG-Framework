package moe.plushie.rpg_framework.bank.common;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.currency.common.Cost;

public class Bank implements IBank, Comparable<IBank> {

    private final IIdentifier identifier;
    private String name;
    private ICost depositCost = Cost.NO_COST;
    private ICost withdrawCost = Cost.NO_COST;
    private int tabSlotCountWidth = 9;
    private int tabSlotCountHeight = 4;
    private int tabStartingCount = 1;
    private int tabMaxCount = 6;
    private int tabIconIndex = 14;
    private ICost[] tabUnlockCosts = null;

    public Bank(IIdentifier identifier) {
        this.identifier = identifier;
        this.name = String.valueOf(identifier.getValue());
    }

    @Override
    public IIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ICost getDepositCost() {
        return depositCost;
    }

    public void setDepositCost(ICost depositCost) {
        this.depositCost = depositCost;
    }

    @Override
    public ICost getWithdrawCost() {
        return withdrawCost;
    }

    public void setWithdrawCost(ICost withdrawCost) {
        this.withdrawCost = withdrawCost;
    }

    @Override
    public int getTabSlotCountWidth() {
        return tabSlotCountWidth;
    }

    public void setTabSlotCountWidth(int tabSlotCountWidth) {
        this.tabSlotCountWidth = tabSlotCountWidth;
    }

    @Override
    public int getTabSlotCountHeight() {
        return tabSlotCountHeight;
    }

    public void setTabSlotCountHeight(int tabSlotCountHeight) {
        this.tabSlotCountHeight = tabSlotCountHeight;
    }

    @Override
    public int getTabSlotCount() {
        return tabSlotCountWidth * tabSlotCountHeight;
    }

    @Override
    public int getTabStartingCount() {
        return tabStartingCount;
    }

    public void setTabStartingCount(int tabStartingCount) {
        this.tabStartingCount = tabStartingCount;
    }

    @Override
    public int getTabMaxCount() {
        return tabMaxCount;
    }

    public void setTabMaxCount(int tabMaxCount) {
        this.tabMaxCount = tabMaxCount;
    }

    @Override
    public int getTabUnlockableCount() {
        return getTabMaxCount() - getTabStartingCount();
    }

    @Override
    public int getTabIconIndex() {
        return tabIconIndex;
    }

    public void setTabIconIndex(int tabIconIndex) {
        this.tabIconIndex = tabIconIndex;
    }

    public void setTabUnlockCosts(ICost[] tabUnlockCosts) {
        this.tabUnlockCosts = tabUnlockCosts;
    }

    @Override
    public ICost getTabUnlockCost(int index) {
        if (tabUnlockCosts != null) {
            if (index >= 0 & index < tabUnlockCosts.length) {
                return tabUnlockCosts[index];
            }
        }
        return Cost.NO_COST;
    }

    @Override
    public int compareTo(IBank o) {
        return this.name.compareTo(o.getName());
    }
}
