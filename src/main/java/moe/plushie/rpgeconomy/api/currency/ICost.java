package moe.plushie.rpgeconomy.api.currency;

import net.minecraft.item.ItemStack;

public interface ICost {
    
    public IWallet getWalletCost();
    
    public ItemStack getItemCost();
}
