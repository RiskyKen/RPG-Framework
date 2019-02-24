package moe.plushie.rpgeconomy.currency.common;

import moe.plushie.rpgeconomy.api.currency.ICurrency;
import moe.plushie.rpgeconomy.api.currency.IWallet;

public class Wallet implements IWallet {

    private final Currency currency;
    private int amount;

    public Wallet(Currency currency, int amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Wallet(Currency currency) {
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
    public String toString() {
        return "Wallet [currency=" + currency + ", amount=" + amount + "]";
    }
}
