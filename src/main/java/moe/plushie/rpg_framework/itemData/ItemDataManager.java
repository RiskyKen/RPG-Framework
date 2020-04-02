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
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;

public final class ItemDataManager implements IItemDataManager {

    private final TableItemData tableItemData = new TableItemData();
    private final TableItemValues tableItemValues = new TableItemValues();
    private final TableTagValues tableTagValues = new TableTagValues();

    public ItemDataManager(File modDirectory) {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                tableItemData.create();
                tableItemValues.create();
                tableTagValues.create();
            }
        });
    }

    public void reload() {
    }

    @Override
    public IItemData getItemData(IItemMatcher itemMatcher) {
        ListenableFutureTask<IItemData> task = getItemDataAsync(itemMatcher, null);
        IItemData itemData = null;
        try {
            itemData = task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemData;
    }

    @Override
    public void setItemData(IItemMatcher itemMatcher, IItemData itemData) {
        DatabaseManager.executeAndWait(new Runnable() {

            @Override
            public void run() {
                tableItemData.setItemData(itemMatcher.getItemStack(), itemMatcher.isMatchMeta(), itemData);
            }
        });
    }

    @Override
    public ListenableFutureTask<IItemData> getItemDataAsync(IItemMatcher itemMatcher, FutureCallback<IItemData> callback) {
        return DatabaseManager.createTaskAndExecute(new Callable<IItemData>() {

            @Override
            public IItemData call() throws Exception {
                IItemData itemData = tableItemData.getItemData(itemMatcher.getItemStack());
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
        }, callback);
    }

    @Override
    public void setItemDataAsync(IItemMatcher itemMatcher, IItemData itemData) {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                tableItemData.setItemData(itemMatcher.getItemStack(), itemMatcher.isMatchMeta(), itemData);
            }
        });
    }
}
