package moe.plushie.rpg_framework.itemData;

import java.io.File;
import java.util.concurrent.Callable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFutureTask;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.api.itemData.IItemDataManager;
import moe.plushie.rpg_framework.core.common.addons.ModAddonManager;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.TableItemData;

public final class ItemDataManager implements IItemDataManager {

    public ItemDataManager(File modDirectory) {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                TableItemData.create();
            }
        });
    }

    public void reload() {
    }

    @Override
    public IItemData getItemData(IItemMatcher itemMatcher) {
        IItemData itemData = TableItemData.getItemData(itemMatcher.getItemStack());
        if (itemData == null) {
            itemData = ItemData.createEmpty();
        }
        // TODO fix cross thread crap below!
        if (ModAddonManager.addonFaerunHeroes.isModLoaded()) {
            ICost cost = ModAddonManager.addonFaerunHeroes.getItemValue(itemMatcher.getItemStack());
            if (!cost.isNoCost() & cost.hasWalletCost()) {
                itemData = itemData.setValue(cost);
            }
        }
        return itemData;
    }

    @Override
    public void setItemData(IItemMatcher itemMatcher, IItemData itemData) {
        TableItemData.setItemData(itemMatcher.getItemStack(), itemMatcher.isMatchMeta(), itemData);
    }

    @Override
    public ListenableFutureTask<IItemData> getItemDataAsync(IItemMatcher itemMatcher, FutureCallback<IItemData> callback) {
        return DatabaseManager.createTaskAndExecute(new Callable<IItemData>() {

            @Override
            public IItemData call() throws Exception {
                return getItemData(itemMatcher);
            }
        }, callback);
    }

    @Override
    public void setItemDataAsync(IItemMatcher itemMatcher, IItemData itemData) {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                setItemData(itemMatcher, itemData);
            }
        });
    }
}
