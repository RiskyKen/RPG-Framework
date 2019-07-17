package moe.plushie.rpg_framework.api.loot;

import net.minecraft.item.ItemStack;

public interface ILootTableItem {
    
    public ItemStack getItem();
    
    public int getWeight();
}
