package moe.plushie.rpgeconomy.api.shop;

import java.util.Date;

import moe.plushie.rpgeconomy.api.currency.IWallet;
import net.minecraft.item.ItemStack;

public interface IShop {

    public String getName();
    
    
    public static interface IShopTab {
        
        public String getName();
        
        public int getIconIndex();
        
        public IShopPage getShopPage(int index);
        
        public int getShopPages();
    }
    
    public static interface IShopPage {
        
        public IShopItem[] getShopItems();
    }
    
    public static interface IShopItem {
        
        public IWallet getCost();
        
        public ItemStack getItem();
        
        public int getStock();
        
        public RestockType getRestockType();
        
        public Date getLastPurchase();
        
        public int getTotalSold();
    }
    
    public static enum RestockType {
        NONE,
        TRIGGER,
        TIME
    }
}
