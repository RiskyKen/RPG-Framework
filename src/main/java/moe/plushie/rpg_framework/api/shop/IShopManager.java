package moe.plushie.rpg_framework.api.shop;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public interface IShopManager {

    public void getShop(IShopLoadCallback callback, IIdentifier identifier);

    public IShop[] getShops();

    public IIdentifier[] getShopIdentifier();

    public String[] getShopNames();

    public static interface IShopLoadCallback {
        public void onShopLoad(IShop shop);
    }
}
