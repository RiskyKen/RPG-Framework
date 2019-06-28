package moe.plushie.rpgeconomy.currency.common.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.CurrencyManager;
import moe.plushie.rpgeconomy.currency.common.Wallet;

public class WalletSerializer {

    private static final String PROP_CURRENCY = "currency";
    private static final String PROP_AMOUNT = "amount";

    private WalletSerializer() {
    }

    public static JsonObject serializeJson(IWallet wallet) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(PROP_CURRENCY, wallet.getCurrency().getName());
        jsonObject.addProperty(PROP_AMOUNT, wallet.getAmount());
        return jsonObject;
    }

    public static Wallet deserializeJson(JsonElement jsonElement) {
        return deserializeJson(jsonElement.getAsJsonObject());
    }

    public static Wallet deserializeJson(JsonObject jsonObject) {
        CurrencyManager currencyManager = RpgEconomy.getProxy().getCurrencyManager();
        try {
            JsonElement propCurrency = jsonObject.get(PROP_CURRENCY);
            JsonElement propAmount = jsonObject.get(PROP_AMOUNT);

            Currency currency = currencyManager.getCurrency(propCurrency.getAsString());
            int amount = propAmount.getAsInt();

            return new Wallet(currency, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
