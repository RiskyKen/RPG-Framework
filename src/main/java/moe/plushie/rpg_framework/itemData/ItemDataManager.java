package moe.plushie.rpg_framework.itemData;

import java.io.File;
import java.util.ArrayList;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.api.itemData.IItemDataManager;
import moe.plushie.rpg_framework.core.common.addons.ModAddonManager;
import moe.plushie.rpg_framework.currency.common.Cost;
import net.minecraft.item.ItemStack;

public final class ItemDataManager implements IItemDataManager {

    private static final String DIRECTORY_NAME = "item_data";

    private final File itemDataDirectory;

    public ItemDataManager(File modDirectory) {
        itemDataDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!itemDataDirectory.exists()) {
            itemDataDirectory.mkdir();
        }
    }

    public void reload() {
    }

    @Override
    public IItemData getItemData(ItemStack itemStack) {
        if (ModAddonManager.addonFaerunHeroes.isModLoaded()) {
            return ModAddonManager.addonFaerunHeroes.getItemData(itemStack);
        }
        return ItemData.ITEM_DATA_MISSING;
    }

    @Override
    public void setItemData(ItemStack itemStack, IItemData itemData) {
        if (itemData == null) {
            itemData = ItemData.ITEM_DATA_MISSING;
        }
        if (ModAddonManager.addonFaerunHeroes.isModLoaded()) {
            ModAddonManager.addonFaerunHeroes.setItemData(itemStack, itemData);
        }
    }

    @Override
    public ArrayList<String> getCategories(ItemStack itemStack) {
        return getItemData(itemStack).getCategories();
    }

    @Override
    public void setCategories(ItemStack itemStack, ArrayList<String> categories) {
        // TODO Auto-generated method stub
    }

    @Override
    public ArrayList<String> getTags(ItemStack itemStack) {
        return getItemData(itemStack).getTags();
    }

    @Override
    public void setTags(ItemStack itemStack, ArrayList<String> tags) {
        // TODO Auto-generated method stub
    }

    @Override
    public ICost getValue(ItemStack itemStack) {
        return getItemData(itemStack).getValue();
    }

    @Override
    public void setValue(ItemStack itemStack, ICost value) {
        if (value == null) {
            value = Cost.NO_COST;
        }
        IItemData itemData = getItemData(itemStack).setValue(value);
        setItemData(itemStack, itemData);
    }
}
