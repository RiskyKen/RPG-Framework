package moe.plushie.rpgeconomy.api.currency;

import net.minecraft.item.ItemStack;

public interface IItemMatcher {

    public boolean matches(ItemStack itemStack);
    
    public ItemStack getItemStack();
}
