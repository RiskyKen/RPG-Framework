package moe.plushie.rpgeconomy.api.shop;

public interface IShopManager {
    
    public IShop getShop(String name);
    
    public IShop[] getShops();
    
    public String[] getShopNames();
}
