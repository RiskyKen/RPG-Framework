package moe.plushie.rpgeconomy.api.loot;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ILootTable {

    public IIdentifier getIdentifier();
    
    public String getName();
    
    public NonNullList<ItemStack> getLoot();
}
