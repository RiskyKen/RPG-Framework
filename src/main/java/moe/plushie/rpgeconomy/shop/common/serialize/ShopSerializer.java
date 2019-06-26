package moe.plushie.rpgeconomy.shop.common.serialize;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.api.shop.IShop.IShopItem;
import moe.plushie.rpgeconomy.api.shop.IShop.IShopTab;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.currency.common.serialize.CostSerializer;
import moe.plushie.rpgeconomy.shop.common.Shop;
import moe.plushie.rpgeconomy.shop.common.Shop.ShopItem;
import moe.plushie.rpgeconomy.shop.common.Shop.ShopTab;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;

public class ShopSerializer {
	
	private static final String PROP_NAME = "name";
	
	private static final String PROP_TABS = "tabs";
	private static final String PROP_TAB_NAME = "name";
	private static final String PROP_TAB_ICON_INDEX = "iconIndex";
	private static final String PROP_TAB_ITEMS = "items";
	
	private static final String PROP_TAB_ITEM = "item";
	private static final String PROP_TAB_ITEM_COST = "cost";
	
	public static JsonElement serializeJson(IShop shop) {
        if (shop == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(PROP_NAME, shop.getName());
        JsonArray jsonArrayTabs = new JsonArray();
        for (IShopTab tab: shop.getTabs()) {
        	JsonObject tabJson = serializeTab(tab);
        	jsonArrayTabs.add(tabJson);
        }
        jsonObject.add(PROP_TABS, jsonArrayTabs);
        
        return jsonObject;
	}
	
	private static JsonObject serializeTab(IShopTab tab) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(PROP_TAB_NAME, tab.getName());
		jsonObject.addProperty(PROP_TAB_ICON_INDEX, tab.getIconIndex());
		JsonArray jsonArrayItems = new JsonArray();
		for (IShopItem item : tab.getItems()) {
			JsonObject jsonItem = serializeItem(item);
			jsonArrayItems.add(jsonItem);
		}
		jsonObject.add(PROP_TAB_ITEMS, jsonArrayItems);
		return jsonObject;
	}
	
	private static JsonObject serializeItem(IShopItem item) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(PROP_TAB_ITEM, SerializeHelper.writeItemToJson(item.getItem()));
		jsonObject.add(PROP_TAB_ITEM_COST, CostSerializer.serializeJson(item.getCost()));
		return jsonObject;
	}
	
	public static Shop deserializeJson(JsonElement json, String identifier) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            String name = jsonObject.get(PROP_NAME).getAsString();
            JsonArray jsonArrayTabs = jsonObject.get(PROP_TABS).getAsJsonArray();
            ArrayList<IShopTab> shopTabs = new ArrayList<IShopTab>();
            for (int i = 0; i < jsonArrayTabs.size(); i++) {
            	shopTabs.add(deserializeTab(jsonArrayTabs.get(i).getAsJsonObject()));
            }
            
            Shop shop = new Shop(identifier, name, shopTabs);
            
            return shop;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	private static IShopTab deserializeTab(JsonObject jsonObject) throws NBTException {
		String name = jsonObject.get(PROP_TAB_NAME).getAsString();
		int iconIndex = jsonObject.get(PROP_TAB_ICON_INDEX).getAsInt();
		JsonArray jsonArrayItems = jsonObject.get(PROP_TAB_ITEMS).getAsJsonArray();
		ArrayList<IShopItem> tabItems = new ArrayList<IShopItem>();
		for (int i = 0; i < jsonArrayItems.size(); i++) {
			tabItems.add(deserializeItem(jsonArrayItems.get(i).getAsJsonObject()));
		}
		
		return new ShopTab(name, iconIndex, tabItems);
	}
	
	private static IShopItem deserializeItem(JsonObject jsonObject) throws NBTException {
		ItemStack item = SerializeHelper.readItemFromJson(jsonObject.get(PROP_TAB_ITEM));
		ICost cost = CostSerializer.deserializeJson(jsonObject.get(PROP_TAB_ITEM_COST));
		return new ShopItem(item, cost);
	}
}
