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
    private final TableItemValues tableItemOverrideValues = new TableItemValues();
    private final TableTagValues tableTagValues = new TableTagValues();

    public ItemDataManager(File modDirectory) {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                tableItemData.create();
                tableItemOverrideValues.create();
                tableTagValues.create();
            }
        });
    }

    public void reload() {
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
    public void setItemDataAsync(IItemMatcher itemMatcher, IItemData itemData) {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                tableItemData.setItemData(itemMatcher.getItemStack(), itemMatcher.isMatchMeta(), itemData);
            }
        });
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
    public ListenableFutureTask<IItemData> getItemDataAsync(IItemMatcher itemMatcher, FutureCallback<IItemData> callback) {
        return DatabaseManager.createTaskAndExecute(new Callable<IItemData>() {

            @Override
            public IItemData call() throws Exception {
                IItemData itemData = tableItemData.getItemData(itemMatcher.getItemStack());
                if (itemData == null) {
                    itemData = ItemData.createEmpty();
                }

                // TODO Add tag cost.

                ICost overrideValue = tableItemOverrideValues.getItemValue(itemMatcher.getItemStack());
                if (!overrideValue.isNoCost() && overrideValue.hasWalletCost()) {
                    itemData = itemData.setValue(overrideValue);
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
    public void setItemOverrideValue(IItemMatcher itemMatcher, ICost value) {
        DatabaseManager.executeAndWait(new Runnable() {

            @Override
            public void run() {
                tableItemOverrideValues.setItemValue(itemMatcher.getItemStack(), itemMatcher.isMatchMeta(), value);
            }
        });
    }

    @Override
    public void setItemOverrideValueAsync(IItemMatcher itemMatcher, ICost value) {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                tableItemOverrideValues.setItemValue(itemMatcher.getItemStack(), itemMatcher.isMatchMeta(), value);
            }
        });
    }

    @Override
    public void clearItemOverrideValue(IItemMatcher itemMatcher) {
        DatabaseManager.executeAndWait(new Runnable() {

            @Override
            public void run() {
                tableItemOverrideValues.clearItemValue(itemMatcher.getItemStack(), itemMatcher.isMatchMeta());
            }
        });
    }

    @Override
    public void clearItemOverrideValueAsync(IItemMatcher itemMatcher) {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                tableItemOverrideValues.clearItemValue(itemMatcher.getItemStack(), itemMatcher.isMatchMeta());
            }
        });
    }
}
