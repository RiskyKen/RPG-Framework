package moe.plushie.rpgeconomy.shop.common;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.NotImplementedException;

import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.currency.common.Wallet;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class Shop implements IShop {

	public static final int ITEMS_PER_PAGE = 8;

	private final String identifier;
	private final String name;
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
	public IShopTab[] getTabs() {
		return shopTabs.toArray(new IShopTab[shopTabs.size()]);
	}

	@Override
	public int getTabCount() {
		return shopTabs.size();
	}

	public static class ShopTab implements IShopTab {

		private final String name;
		private final int iconIndex;
		private final ArrayList<IShopItem> shopItems;

		public ShopTab(String name,  int iconIndex, ArrayList<IShopItem> shopItems) {
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
		public IShopItem[] getPageItems(int pageIndex) {
			return null;
		}

		@Override
		public IShopItem[] getItems() {
			return shopItems.toArray(new IShopItem[shopItems.size()]);
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
		private final IWallet cost;

		public ShopItem(ItemStack item, IWallet cost) {
			this.item = item;
			this.cost = cost;
		}

		@Override
		public ItemStack getItem() {
			return item;
		}

		@Override
		public IWallet getCost() {
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
