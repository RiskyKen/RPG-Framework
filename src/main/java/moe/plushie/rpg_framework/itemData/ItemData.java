package moe.plushie.rpg_framework.itemData;

import java.util.ArrayList;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.currency.common.Cost;

public class ItemData implements IItemData {

    public static final ItemData ITEM_DATA_MISSING = new ItemData();

    public ArrayList<String> categories = new ArrayList<String>();
    public ArrayList<String> tags = new ArrayList<String>();
    public ICost value = Cost.NO_COST;

    private ItemData() {
    }

    private ItemData(ArrayList<String> categories, ArrayList<String> tags, ICost value) {
        this.categories = categories;
        this.tags = tags;
        this.value = value;
    }

    public static ItemData create(ArrayList<String> categories, ArrayList<String> tags, ICost value) {
        return new ItemData(categories, tags, value);
    }
    
    public static ItemData createEmpty() {
        return new ItemData(new ArrayList<String>(), new ArrayList<String>(), Cost.NO_COST);
    }

    @Override
    public ArrayList<String> getCategories() {
        return categories;
    }

    @Override
    public ArrayList<String> getTags() {
        return tags;
    }

    @Override
    public ICost getValue() {
        return value;
    }

    @Override
    public IItemData setCategories(ArrayList<String> categories) {
        return new ItemData(categories, this.tags, this.value);
    }

    @Override
    public IItemData setTags(ArrayList<String> tags) {
        return new ItemData(this.categories, tags, this.value);
    }

    @Override
    public IItemData setValue(ICost value) {
        return new ItemData(this.categories, this.tags, value);
    }
    
    @Override
    public boolean isDataMissing() {
        if (this == ITEM_DATA_MISSING) {
            return true;
        }
        if (this.categories.isEmpty() & this.tags.isEmpty() & this.value.isNoCost()) {
            return true;
        }
        return false;
    }
}
