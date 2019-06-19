package moe.plushie.rpgeconomy.api.shop;

import java.util.Date;

import moe.plushie.rpgeconomy.api.currency.IWallet;
import net.minecraft.item.ItemStack;

public interface IShop {

	public String getIdentifier();
	
	public String getName();

	public IShopTab[] getTabs();
	
	public int getTabCount();

	public static interface IShopTab {

		public String getName();

		public int getIconIndex();

		public IShopItem[] getPageItems(int pageIndex);
		
		public IShopItem[] getItems();
		
		public int getItemCount();

		public int getPageCount();
	}

	public static interface IShopItem {
		
		public ItemStack getItem();
		
		public IWallet getCost();

		public int getStock();

		public RestockType getRestockType();

		public Date getLastPurchase();

		public int getTotalSold();
	}

	public static enum RestockType {
		NONE, TRIGGER, TIME
	}
}
