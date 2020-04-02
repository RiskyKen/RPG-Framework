package moe.plushie.rpg_framework.api.itemData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFutureTask;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.currency.ICost;

public interface IItemDataManager {

    public void setItemData(@Nonnull IItemMatcher itemMatcher, @Nonnull IItemData itemData);

    public void setItemDataAsync(@Nonnull IItemMatcher itemMatcher, @Nonnull IItemData itemData);

    public IItemData getItemData(@Nonnull IItemMatcher itemMatcher);

    public ListenableFutureTask<IItemData> getItemDataAsync(@Nonnull IItemMatcher itemMatcher, @Nullable FutureCallback<IItemData> callback);

    public void setItemOverrideValue(@Nonnull IItemMatcher itemMatcher, @Nonnull ICost value);

    public void setItemOverrideValueAsync(@Nonnull IItemMatcher itemMatcher, @Nonnull ICost value);
    
    public void clearItemOverrideValue(@Nonnull IItemMatcher itemMatcher);
    
    public void clearItemOverrideValueAsync(@Nonnull IItemMatcher itemMatcher);
}
