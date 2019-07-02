package moe.plushie.rpgeconomy.shop.common;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.NotImplementedException;

import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.currency.common.Cost;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class Shop implements IShop {

    public static final int ITEMS_PER_PAGE = 8;

    private final String identifier;
    private String name;
    private final ArrayList<IShopTab> shopTabs;

    public Shop(String identifier, String name, ArrayList<IShopTab> shopTabs) {
        this.identifier = identifier;
        this.name = name;
        this.shopTabs = shopTabs;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ArrayList<IShopTab> getTabs() {
        return shopTabs;
    }

    @Override
    public int getTabCount() {
        return shopTabs.size();
    }

    public static class ShopTab implements IShopTab {

        private final String name;
        private final int iconIndex;
        private final ArrayList<IShopItem> shopItems;

        public ShopTab(String name, int iconIndex) {
            this.name = name;
            this.iconIndex = iconIndex;
            this.shopItems = new ArrayList<IShop.IShopItem>();
            for (int i = 0; i < 8; i++) {
                shopItems.add(new ShopItem(ItemStack.EMPTY, new Cost(null, null)));
            }
        }

        public ShopTab(String name, int iconIndex, ArrayList<IShopItem> shopItems) {
            this.name = name;
            this.iconIndex = iconIndex;
            this.shopItems = shopItems;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getIconIndex() {
            return iconIndex;
        }

        @Override
        public ArrayList<IShopItem> getPageItems(int pageIndex) {
            return null;
        }

        @Override
        public ArrayList<IShopItem> getItems() {
            return shopItems;
        }

        @Override
        public int getItemCount() {
            return shopItems.size();
        }

        @Override
        public int getPageCount() {
            return MathHelper.ceil((double) getItemCount() / (double) ITEMS_PER_PAGE);
        }
    }

    public static class ShopItem implements IShopItem {

        private final ItemStack item;
        private final ICost cost;

        public ShopItem(ItemStack item, ICost cost) {
            this.item = item;
            this.cost = cost;
        }

        @Override
        public ItemStack getItem() {
            return item;
        }

        @Override
        public ICost getCost() {
            return cost;
        }

        @Override
        public int getStock() {
            throw new NotImplementedException("getStock() is not implemented yet.");
        }

        @Override
        public RestockType getRestockType() {
            throw new NotImplementedException("getRestockType() is not implemented yet.");
        }

        @Override
        public Date getLastPurchase() {
            throw new NotImplementedException("getLastPurchase() is not implemented yet.");
        }

        @Override
        public int getTotalSold() {
            throw new NotImplementedException("getTotalSold() is not implemented yet.");
        }
    }
}
