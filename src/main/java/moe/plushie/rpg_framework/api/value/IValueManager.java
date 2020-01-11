package moe.plushie.rpg_framework.api.value;

import moe.plushie.rpg_framework.api.currency.ICost;
import net.minecraft.item.ItemStack;

public interface IValueManager {
    
    public ICost getValue(ItemStack itemStack);
    
    public void setValue(ItemStack itemStack, ICost cost);
}
