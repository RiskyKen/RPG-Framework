package moe.plushie.rpg_framework.api.shop;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public interface IShopManager {
    
    public IShop getShop(IIdentifier identifier);
    
    public IShop[] getShops();
    
    public IIdentifier[] getShopIdentifier();
    
    public String[] getShopNames();
}
