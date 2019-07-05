package moe.plushie.rpgeconomy.api.shop;

import moe.plushie.rpgeconomy.api.core.IIdentifier;

public interface IShopManager {
    
    public IShop getShop(IIdentifier identifier);
    
    public IShop[] getShops();
    
    public IIdentifier[] getShopIdentifier();
    
    public String[] getShopNames();
}
