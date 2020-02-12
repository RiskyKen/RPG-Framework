package moe.plushie.rpg_framework.api.itemData;

import java.util.ArrayList;

import moe.plushie.rpg_framework.api.currency.ICost;
import net.minecraft.item.ItemStack;

public interface IItemDataManager {
    
    public IItemData getItemData(ItemStack itemStack);
    
    public void setItemData(ItemStack itemStack, IItemData itemData);
    
    public ArrayList<String> getCategories(ItemStack itemStack);
    
    public void setCategories(ItemStack itemStack, ArrayList<String> categories);
    
    public ArrayList<String> getTags(ItemStack itemStack);
    
    public void setTags(ItemStack itemStack, ArrayList<String> tags);
    
    public ICost getValue(ItemStack itemStack);
    
    public void setValue(ItemStack itemStack, ICost cost);
}
