package moe.plushie.rpgeconomy.currency.common;

import moe.plushie.rpgeconomy.api.currency.ICurrency;
import moe.plushie.rpgeconomy.api.currency.IWallet;

public class Wallet implements IWallet {

    private final ICurrency currency;
    private int amount;

    public Wallet(ICurrency currency, int amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Wallet(ICurrency currency) {
        this(currency, 0);
    }

    @Override
    public ICurrency getCurrency() {
        return currency;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int getAmount() {
        return amount;
    }
    
    @Override
    public void addAmount(int amount) {
        if ((long)amount + (long)this.amount > (long)Integer.MAX_VALUE) {
            this.amount = Integer.MAX_VALUE;
        } else {
            this.amount += amount;
        }
    }

    @Override
    public void removeAmount(int amount) {
        this.amount -= amount;
        if (this.amount < 0) {
            this.amount = 0;
        }
    }
    
    @Override
    public String toString() {
        return "Wallet [currency=" + currency + ", amount=" + amount + "]";
    }
}
