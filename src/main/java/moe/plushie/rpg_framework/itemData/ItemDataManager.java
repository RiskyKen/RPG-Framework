package moe.plushie.rpg_framework.itemData;

import java.io.File;
import java.util.concurrent.Callable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
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
        DatabaseManager.EXECUTOR.execute(new Runnable() {
            
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
        IItemData itemData = TableItemData.getItemData(itemMatcher);
        if (itemData == null) {
            itemData = ItemData.createEmpty();
        }
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
        TableItemData.setItemData(itemMatcher, itemData);
    }

    @Override
    public ListenableFutureTask<IItemData> getItemDataAsync(IItemMatcher itemMatcher, FutureCallback<IItemData> callback) {
        ListenableFutureTask task = ListenableFutureTask.<IItemData>create(new Async(itemMatcher));
        if (callback != null) {
            Futures.addCallback(task, callback);
        }
        DatabaseManager.EXECUTOR.execute(task);
        return task;
    }

    @Override
    public void setItemDataAsync(IItemMatcher itemMatcher, IItemData itemData) {
        DatabaseManager.EXECUTOR.execute(new Runnable() {

            @Override
            public void run() {
                setItemData(itemMatcher, itemData);
            }
        });
    }

    private class Async implements Callable<IItemData> {

        private final IItemMatcher itemMatcher;

        public Async(IItemMatcher itemMatcher) {
            this.itemMatcher = itemMatcher;
        }

        @Override
        public IItemData call() throws Exception {
            return getItemData(itemMatcher);
        }
    }
}
