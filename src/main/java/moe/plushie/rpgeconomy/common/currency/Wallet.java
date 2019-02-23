package moe.plushie.rpgeconomy.common.currency;

public class Wallet {

    private final Currency currency;
    private int amount;
    
    public Wallet(Currency currency, int amount) {
        this.currency = currency;
        this.amount = amount;
    }
    
    public Wallet(Currency currency) {
        this(currency, 0);
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Wallet [currency=" + currency + ", amount=" + amount + "]";
    }
}
