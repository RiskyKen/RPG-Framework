package moe.plushie.rpg_framework.currency.common.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpg_framework.currency.common.CurrencyManager;
import moe.plushie.rpg_framework.currency.common.Wallet;

public class WalletSerializer {

    private static final String PROP_CURRENCY = "currency";
    private static final String PROP_AMOUNT = "amount";

    private WalletSerializer() {
    }

    public static JsonObject serializeJson(IWallet wallet) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(PROP_CURRENCY, IdentifierSerialize.serializeJson(wallet.getCurrency().getIdentifier()));
        jsonObject.addProperty(PROP_AMOUNT, wallet.getAmount());
        return jsonObject;
    }

    public static Wallet deserializeJson(JsonElement jsonElement) {
        return deserializeJson(jsonElement.getAsJsonObject());
    }

    public static Wallet deserializeJson(JsonObject jsonObject) {
        CurrencyManager currencyManager = RPGFramework.getProxy().getCurrencyManager();
        try {
            ICurrency currency = null;
            int amount = 0;

            if (jsonObject.has(PROP_CURRENCY)) {
                IIdentifier identifier;

                try {
                    identifier = new IdentifierString(jsonObject.get(PROP_CURRENCY).getAsString());
                    currency = currencyManager.getCurrency(identifier);
                } catch (Exception e) {
                    // Trying to load using old save system.
                }

                // Load using the new save system.
                if (currency == null) {
                    currency = currencyManager.getCurrency(IdentifierSerialize.deserializeJson(jsonObject.get(PROP_CURRENCY)));
                }
            }

            if (jsonObject.has(PROP_AMOUNT)) {
                amount = jsonObject.get(PROP_AMOUNT).getAsInt();
            }

            return new Wallet(currency, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
