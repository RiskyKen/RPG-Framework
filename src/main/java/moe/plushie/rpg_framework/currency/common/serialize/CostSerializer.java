package moe.plushie.rpg_framework.currency.common.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.core.common.serialize.ItemMacherSerializer;
import moe.plushie.rpg_framework.currency.common.Cost;

public final class CostSerializer {

    private static final String PROP_CURRENCY = "currency";
    private static final String PROP_ITEMS = "items";

    private CostSerializer() {
    }

    public static JsonObject serializeJson(ICost cost, boolean compact) {
        JsonObject jsonObject = new JsonObject();
        // Write items
        JsonArray arrayItems = new JsonArray();
        if (cost.hasItemCost()) {
            for (int i = 0; i < cost.getItemCost().length; i++) {
                arrayItems.add(ItemMacherSerializer.serializeJson(cost.getItemCost()[i], compact));
            }
            jsonObject.add(PROP_ITEMS, arrayItems);
        }

        // Write wallet.
        if (cost.hasWalletCost()) {
            jsonObject.add(PROP_CURRENCY, WalletSerializer.serializeJson(cost.getWalletCost()));
        }
        return jsonObject;
    }

    public static ICost deserializeJson(JsonElement jsonElement) {
        try {
            return deserializeJson(jsonElement.getAsJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Cost.NO_COST;
    }

    public static ICost deserializeJson(JsonObject jsonObject) {
        try {
            IItemMatcher[] itemCost = null;
            IWallet walletCost = null;
            // Read items.
            if (jsonObject.has(PROP_ITEMS)) {
                JsonArray arrayItems = jsonObject.get(PROP_ITEMS).getAsJsonArray();
                itemCost = new IItemMatcher[arrayItems.size()];
                for (int i = 0; i < arrayItems.size(); i++) {
                    itemCost[i] = ItemMacherSerializer.deserializeJson(arrayItems.get(i));
                }
            }
            // Read wallet.
            if (jsonObject.has(PROP_CURRENCY)) {
                walletCost = WalletSerializer.deserializeJson(jsonObject.get(PROP_CURRENCY).getAsJsonObject());
            }

            return new Cost(walletCost, itemCost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Cost.NO_COST;
    }
}
