package moe.plushie.rpgeconomy.currency.serialize;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.Currency.CurrencyVariant;
import net.minecraft.item.ItemStack;

public final class CurrencySerializer {

    private static final String PROP_NAME = "name";
    private static final String PROP_HAS_WALLET = "hasWallet";
    private static final String PROP_NEED_ITEM_TO_OPEN = "needItemToAccess";
    private static final String PROP_OPEN_WITH_KEYBIND = "opensWithKeybind";
    private static final String PROP_PICKUP_INTO_WALLET = "pickupIntoWallet";
    private static final String PROP_VARIANTS = "variants";

    private static final String PROP_VAR_NAME = "name";
    private static final String PROP_VAR_VALUE = "value";
    private static final String PROP_VAR_ITEM = "item";

    private CurrencySerializer() {
    }

    public static JsonElement serializeJson(Currency currency) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(PROP_NAME, currency.getName());
        jsonObject.addProperty(PROP_HAS_WALLET, currency.getHasWallet());
        jsonObject.addProperty(PROP_NEED_ITEM_TO_OPEN, currency.getNeedItemToAccess());
        jsonObject.addProperty(PROP_OPEN_WITH_KEYBIND, currency.getOpensWithKeybind());
        jsonObject.addProperty(PROP_PICKUP_INTO_WALLET, currency.getPickupIntoWallet());

        JsonArray jsonVariants = new JsonArray();
        CurrencyVariant[] variants = currency.getCurrencyVariants();
        for (int i = 0; i < variants.length; i++) {
            JsonObject jsonVariant = new JsonObject();
            
            jsonVariant.addProperty(PROP_VAR_NAME, variants[i].getName());
            jsonVariant.addProperty(PROP_VAR_VALUE, variants[i].getValue());
            jsonVariant.add(PROP_VAR_ITEM, SerializeHelper.writeItemToJson(variants[i].getItem()));
            
            jsonVariants.add(jsonVariant);
        }
        jsonObject.add(PROP_VARIANTS, jsonVariants);

        return jsonObject;
    }

    public static Currency deserializeJson(JsonElement json) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            JsonElement propName = jsonObject.get(PROP_NAME);
            JsonElement propHasWallet = jsonObject.get(PROP_HAS_WALLET);
            JsonElement propNeedItemToOpen = jsonObject.get(PROP_NEED_ITEM_TO_OPEN);
            JsonElement propOpensWithKeybind = jsonObject.get(PROP_OPEN_WITH_KEYBIND);
            JsonElement propPickupIntoWallet = jsonObject.get(PROP_PICKUP_INTO_WALLET);

            String name = propName.getAsString();
            boolean hasWallet = propHasWallet.getAsBoolean();
            boolean needItemToAccess = propNeedItemToOpen.getAsBoolean();
            boolean opensWithKeybind = propOpensWithKeybind.getAsBoolean();
            boolean pickupIntoWallet = propPickupIntoWallet.getAsBoolean();

            JsonElement propVariants = jsonObject.get(PROP_VARIANTS);
            JsonArray jsonVariants = propVariants.getAsJsonArray();

            ArrayList<CurrencyVariant> variants = new ArrayList<CurrencyVariant>();
            for (int i = 0; i < jsonVariants.size(); i++) {
                JsonObject jsonVariant = jsonVariants.get(i).getAsJsonObject();
                
                JsonElement propVariantName = jsonVariant.get(PROP_VAR_NAME);
                JsonElement propVariantValue = jsonVariant.get(PROP_VAR_VALUE);
                JsonElement propVariantItem = jsonVariant.get(PROP_VAR_ITEM);

                String variantName = propVariantName.getAsString();
                int variantValue = propVariantValue.getAsInt();
                ItemStack itemStack = SerializeHelper.readItemFromJson(propVariantItem);
                
                variants.add(new CurrencyVariant(variantName, variantValue, itemStack));
            }
            Collections.sort(variants);
            return new Currency(name, hasWallet, needItemToAccess, opensWithKeybind, pickupIntoWallet, variants.toArray(new CurrencyVariant[variants.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
