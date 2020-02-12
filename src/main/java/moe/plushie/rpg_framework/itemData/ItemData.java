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
    }

    public static ItemData create(ArrayList<String> categories, ArrayList<String> tags, ICost value) {
        return new ItemData(categories, tags, value);
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
}
