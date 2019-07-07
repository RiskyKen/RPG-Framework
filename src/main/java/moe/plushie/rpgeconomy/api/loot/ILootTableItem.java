package moe.plushie.rpgeconomy.api.loot;

import net.minecraft.item.ItemStack;

public interface ILootTableItem {
    
    public ItemStack getItem();
    
    public int getWeight();
}
