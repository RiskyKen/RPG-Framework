package moe.plushie.rpgeconomy.api.shop;

import java.util.ArrayList;
import java.util.Date;

import moe.plushie.rpgeconomy.api.currency.ICost;
import net.minecraft.item.ItemStack;

public interface IShop {

	public String getIdentifier();
	
	public String getName();

	public ArrayList<IShopTab> getTabs();
	
	public int getTabCount();

	public static interface IShopTab {

		public String getName();

		public int getIconIndex();

		public ArrayList<IShopItem> getPageItems(int pageIndex);
		
		public ArrayList<IShopItem> getItems();
		
		public int getItemCount();

		public int getPageCount();
	}

	public static interface IShopItem {
		
		public ItemStack getItem();
		
		public ICost getCost();

		public int getStock();

		public RestockType getRestockType();

		public Date getLastPurchase();

		public int getTotalSold();
	}

	public static enum RestockType {
		NONE, TRIGGER, TIME
	}
}
