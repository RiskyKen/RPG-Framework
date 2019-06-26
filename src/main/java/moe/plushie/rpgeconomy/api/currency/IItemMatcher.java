package moe.plushie.rpgeconomy.api.currency;

import net.minecraft.item.ItemStack;

public interface IItemMatcher {

    public ItemStack getItemStack();
    
    public boolean matchMeta();
    
    public boolean matchNBT();
}
