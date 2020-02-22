package moe.plushie.rpg_framework.api.shop;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFutureTask;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public interface IShopManager {

    public void saveShop(IShop shop);

    public ListenableFutureTask<Void> saveShopAsync(IShop shop, @Nullable FutureCallback<Void> callback);

    public IShop getShop(IIdentifier identifier);

    public ListenableFutureTask<IShop> getShopAsync(IIdentifier identifier, @Nullable FutureCallback<IShop> callback);

    public IShop createShop(String shopName);

    public ListenableFutureTask<IShop> createShopAsync(String shopName, @Nullable FutureCallback<IShop> callback);

    public void removeShop(IIdentifier identifier);

    public ListenableFutureTask<Void> removeShopAsync(IIdentifier identifier, @Nullable FutureCallback<Void> callback);
}
