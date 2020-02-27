package moe.plushie.rpg_framework.api.itemData;

import java.util.ArrayList;

import moe.plushie.rpg_framework.api.currency.ICost;

public interface IItemData {

    public ArrayList<String> getCategories();

    public ArrayList<String> getTags();

    public ICost getValue();
    
    public IItemData setCategories(ArrayList<String> categories);
    
    public IItemData setTags(ArrayList<String> tags);
    
    public IItemData setValue(ICost value);
    
    public boolean isDataMissing();
}
