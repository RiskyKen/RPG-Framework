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
        if (cost.hasItemCost()) {
            JsonArray arrayItems = new JsonArray();
            for (int i = 0; i < cost.getItemCosts().length; i++) {
                arrayItems.add(ItemMacherSerializer.serializeJson(cost.getItemCosts()[i], compact));
            }
            jsonObject.add(PROP_ITEMS, arrayItems);
        }

        // Write wallet.
        if (cost.hasWalletCost()) {
            JsonArray arrayWallets = new JsonArray();
            for (int i = 0; i < cost.getWalletCosts().length; i++) {
                arrayWallets.add(WalletSerializer.serializeJson(cost.getWalletCosts()[i]));
            }
            jsonObject.add(PROP_CURRENCY, arrayWallets);
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
            IWallet[] walletCost = null;
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
                if (jsonObject.get(PROP_CURRENCY).isJsonObject()) {
                    walletCost = new IWallet[] { WalletSerializer.deserializeJson(jsonObject.get(PROP_CURRENCY).getAsJsonObject()) };
                }
                if (jsonObject.get(PROP_CURRENCY).isJsonArray()) {
                    JsonArray arrayWallets = jsonObject.get(PROP_CURRENCY).getAsJsonArray();
                    walletCost = new IWallet[arrayWallets.size()];
                    for (int i = 0; i < arrayWallets.size(); i++) {
                        walletCost[i] = WalletSerializer.deserializeJson(arrayWallets.get(i));
                    }
                }
            }

            return new Cost(walletCost, itemCost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Cost.NO_COST;
    }
}
