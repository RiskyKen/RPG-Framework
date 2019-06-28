package moe.plushie.rpgeconomy.currency.common.serialize;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpgeconomy.api.currency.ICurrency.ICurrencyWalletInfo;
import moe.plushie.rpgeconomy.api.currency.IItemMatcher;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.Currency.CurrencyVariant;
import moe.plushie.rpgeconomy.currency.common.Currency.CurrencyWalletInfo;
import net.minecraft.nbt.NBTException;

public final class CurrencySerializer {

    private static final String PROP_NAME = "name";
    private static final String PROP_DISPLAY_FORMAT = "displayFormat";

    private static final String PROP_WALLET = "wallet";
    private static final String PROP_CREATE_WALLET_ITEM = "createWalletItem";
    private static final String PROP_NEED_ITEM_TO_ACCESS = "needItemToAccess";
    private static final String PROP_MOD_KEYBIND = "modKeybind";
    private static final String PROP_PICKUP_INTO_WALLET = "pickupIntoWallet";
    private static final String PROP_DEATH_PERCENTAGE_DROPPED = "deathPercentageDropped";
    private static final String PROP_DEATH_PERCENTAGE_LOST = "deathPercentageLost";

    private static final String PROP_VARIANTS = "variants";
    private static final String PROP_VAR_NAME = "name";
    private static final String PROP_VAR_VALUE = "value";

    private CurrencySerializer() {
    }

    public static JsonElement serializeJson(Currency currency) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(PROP_NAME, currency.getName());
        jsonObject.addProperty(PROP_DISPLAY_FORMAT, currency.getDisplayFormat());
        jsonObject.add(PROP_WALLET, serializeCurrencyWalletInfo(currency.getCurrencyWalletInfo()));

        JsonArray jsonVariants = new JsonArray();
        CurrencyVariant[] variants = currency.getCurrencyVariants();
        for (int i = 0; i < variants.length; i++) {
            jsonVariants.add(serializeCurrencyVariant(variants[i]));
        }
        jsonObject.add(PROP_VARIANTS, jsonVariants);
        return jsonObject;
    }

    public static JsonObject serializeCurrencyVariant(CurrencyVariant variant) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(PROP_VAR_NAME, variant.getName());
        jsonObject.addProperty(PROP_VAR_VALUE, variant.getValue());
        jsonObject.add(ItemMacherSerializer.PROP_ITEM_MATCHER, ItemMacherSerializer.serializeJson(variant.getItem()));
        return jsonObject;
    }

    public static JsonObject serializeCurrencyWalletInfo(ICurrencyWalletInfo walletInfo) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(PROP_CREATE_WALLET_ITEM, walletInfo.getCreateWalletItem());
        jsonObject.addProperty(PROP_NEED_ITEM_TO_ACCESS, walletInfo.getNeedItemToAccess());
        jsonObject.addProperty(PROP_MOD_KEYBIND, walletInfo.getModKeybind());
        jsonObject.addProperty(PROP_PICKUP_INTO_WALLET, walletInfo.getPickupIntoWallet());
        jsonObject.addProperty(PROP_DEATH_PERCENTAGE_DROPPED, walletInfo.getDeathPercentageDropped());
        jsonObject.addProperty(PROP_DEATH_PERCENTAGE_LOST, walletInfo.getDeathPercentageLost());
        return jsonObject;
    }

    public static Currency deserializeJson(JsonElement json) {
        try {
            JsonObject jsonCurrency = json.getAsJsonObject();

            String name = jsonCurrency.get(PROP_NAME).getAsString();
            String displayFormat = jsonCurrency.get(PROP_DISPLAY_FORMAT).getAsString();
            CurrencyWalletInfo walletInfo = deserializeCurrencyWalletInfo(jsonCurrency.get(PROP_WALLET).getAsJsonObject());
            JsonArray jsonVariants = jsonCurrency.get(PROP_VARIANTS).getAsJsonArray();
            ArrayList<CurrencyVariant> variants = new ArrayList<CurrencyVariant>();
            for (int i = 0; i < jsonVariants.size(); i++) {
                variants.add(deserializeCurrencyVariant(jsonVariants.get(i).getAsJsonObject()));
            }
            Collections.sort(variants);
            return new Currency(name, name, displayFormat, walletInfo, variants.toArray(new CurrencyVariant[variants.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static CurrencyVariant deserializeCurrencyVariant(JsonObject jsonObject) throws NBTException {
        JsonElement propVariantName = jsonObject.get(PROP_VAR_NAME);
        JsonElement propVariantValue = jsonObject.get(PROP_VAR_VALUE);
        JsonElement propVariantItem = jsonObject.get(ItemMacherSerializer.PROP_ITEM_MATCHER);
        String variantName = propVariantName.getAsString();
        int variantValue = propVariantValue.getAsInt();
        IItemMatcher variantItemStack = ItemMacherSerializer.deserializeJson(propVariantItem);
        return new CurrencyVariant(variantName, variantValue, variantItemStack);
    }

    private static CurrencyWalletInfo deserializeCurrencyWalletInfo(JsonObject jsonObject) {
        boolean createWalletItem = jsonObject.get(PROP_CREATE_WALLET_ITEM).getAsBoolean();
        boolean needItemToAccess = jsonObject.get(PROP_NEED_ITEM_TO_ACCESS).getAsBoolean();
        String modKeybind = jsonObject.get(PROP_MOD_KEYBIND).getAsString();
        boolean pickupIntoWallet = jsonObject.get(PROP_PICKUP_INTO_WALLET).getAsBoolean();
        float deathPercentageDropped = jsonObject.get(PROP_DEATH_PERCENTAGE_DROPPED).getAsFloat();
        float deathPercentageLost = jsonObject.get(PROP_DEATH_PERCENTAGE_LOST).getAsFloat();
        return new CurrencyWalletInfo(createWalletItem, needItemToAccess, modKeybind, pickupIntoWallet, deathPercentageDropped, deathPercentageLost);
    }
}
