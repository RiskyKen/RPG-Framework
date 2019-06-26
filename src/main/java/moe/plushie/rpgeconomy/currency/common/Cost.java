package moe.plushie.rpgeconomy.currency.common;

import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.api.currency.IWallet;
import net.minecraft.item.ItemStack;

public class Cost implements ICost {

    private final IWallet walletCost;
    private final ItemStack[] itemCost;
    
    public Cost(IWallet walletCost, ItemStack[] itemCost) {
        this.walletCost = walletCost;
        this.itemCost = itemCost;
    }
    
    @Override
    public IWallet getWalletCost() {
        return walletCost;
    }

    @Override
    public ItemStack[] getItemCost() {
        return itemCost;
    }
    
    @Override
    public boolean hasWalletCost() {
        return walletCost != null;
    }
    
    @Override
    public boolean hasItemCost() {
        return itemCost != null;
    }
}
