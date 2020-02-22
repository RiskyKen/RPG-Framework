package moe.plushie.rpg_framework.api.itemData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFutureTask;

import moe.plushie.rpg_framework.api.core.IItemMatcher;

public interface IItemDataManager {

    public IItemData getItemData(@Nonnull IItemMatcher itemMatcher);

    public void setItemData(@Nonnull IItemMatcher itemMatcher, @Nonnull IItemData itemData);

    public ListenableFutureTask<IItemData> getItemDataAsync(@Nonnull IItemMatcher itemMatcher, @Nullable FutureCallback<IItemData> callback);

    public void setItemDataAsync(@Nonnull IItemMatcher itemMatcher, @Nonnull IItemData itemData);
}
