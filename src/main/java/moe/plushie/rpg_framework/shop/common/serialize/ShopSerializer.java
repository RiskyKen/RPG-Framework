package moe.plushie.rpg_framework.shop.common.serialize;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.shop.IShop;
import moe.plushie.rpg_framework.api.shop.IShop.IShopItem;
import moe.plushie.rpg_framework.api.shop.IShop.IShopTab;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.serialize.CostSerializer;
import moe.plushie.rpg_framework.shop.common.Shop;
import moe.plushie.rpg_framework.shop.common.Shop.ShopItem;
import moe.plushie.rpg_framework.shop.common.Shop.ShopTab;
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
    private static final String PROP_TAB_ITEM_SLOT = "slot";

    public static JsonElement serializeJson(IShop shop, boolean compact) {
        if (shop == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(PROP_NAME, shop.getName());
        jsonObject.add(PROP_TABS, serializeTabs(shop.getTabs(), compact));

        return jsonObject;
    }

    public static JsonArray serializeTabs(ArrayList<IShopTab> shopTabs, boolean compact) {
        JsonArray jsonArrayTabs = new JsonArray();
        for (IShopTab tab : shopTabs) {
            JsonObject tabJson = serializeTab(tab, compact);
            jsonArrayTabs.add(tabJson);
        }
        return jsonArrayTabs;
    }

    private static JsonObject serializeTab(IShopTab tab, boolean compact) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(PROP_TAB_NAME, tab.getName());
        jsonObject.addProperty(PROP_TAB_ICON_INDEX, tab.getIconIndex());
        JsonArray jsonArrayItems = new JsonArray();
        for (int i = 0; i < tab.getItems().size(); i++) {
            IShopItem item = tab.getItems().get(i);
            if (!item.getItem().isEmpty()) {
                JsonObject jsonItem = serializeItem(item, compact);
                jsonItem.addProperty(PROP_TAB_ITEM_SLOT, i);
                jsonArrayItems.add(jsonItem);
            }
        }
        jsonObject.add(PROP_TAB_ITEMS, jsonArrayItems);
        return jsonObject;
    }

    private static JsonObject serializeItem(IShopItem item, boolean compact) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(PROP_TAB_ITEM, SerializeHelper.writeItemToJson(item.getItem(), compact));
        jsonObject.add(PROP_TAB_ITEM_COST, CostSerializer.serializeJson(item.getCost(), compact));
        return jsonObject;
    }

    public static Shop deserializeJson(JsonElement json, IIdentifier identifier) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            String name = jsonObject.get(PROP_NAME).getAsString();
            JsonArray jsonArrayTabs = jsonObject.get(PROP_TABS).getAsJsonArray();
            ArrayList<IShopTab> shopTabs = deserializeTabs(jsonArrayTabs);
            Shop shop = new Shop(identifier, name, shopTabs);

            return shop;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<IShopTab> deserializeTabs(JsonArray jsonArray) throws NBTException {
        ArrayList<IShopTab> shopTabs = new ArrayList<IShopTab>();
        for (int i = 0; i < jsonArray.size(); i++) {
            shopTabs.add(deserializeTab(jsonArray.get(i).getAsJsonObject()));
        }
        return shopTabs;
    }

    private static IShopTab deserializeTab(JsonObject jsonObject) throws NBTException {
        String name = jsonObject.get(PROP_TAB_NAME).getAsString();
        int iconIndex = jsonObject.get(PROP_TAB_ICON_INDEX).getAsInt();
        JsonArray jsonArrayItems = jsonObject.get(PROP_TAB_ITEMS).getAsJsonArray();
        ArrayList<IShopItem> tabItems = new ArrayList<IShopItem>();
        for (int i = 0; i < 8; i++) {
            tabItems.add(new ShopItem(ItemStack.EMPTY, new Cost(null, null)));
        }
        for (int i = 0; i < jsonArrayItems.size(); i++) {
            JsonObject itemJson = jsonArrayItems.get(i).getAsJsonObject();
            IShopItem item = deserializeItem(jsonArrayItems.get(i).getAsJsonObject());
            if (itemJson.has(PROP_TAB_ITEM_SLOT)) {
                tabItems.set(itemJson.get(PROP_TAB_ITEM_SLOT).getAsInt(), item);
            } else {
                tabItems.set(i, item);
            }
        }

        return new ShopTab(name, iconIndex, tabItems);
    }

    private static IShopItem deserializeItem(JsonObject jsonObject) throws NBTException {
        ItemStack item = SerializeHelper.readItemFromJson(jsonObject.get(PROP_TAB_ITEM));
        ICost cost = CostSerializer.deserializeJson(jsonObject.get(PROP_TAB_ITEM_COST));
        return new ShopItem(item, cost);
    }
}
