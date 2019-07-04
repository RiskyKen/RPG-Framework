package moe.plushie.rpgeconomy.api.shop;

public interface IShopManager {
    
    public IShop getShop(String identifier);
    
    public IShop[] getShops();
    
    public String[] getShopIdentifier();
    
    public String[] getShopNames();
}
