package moe.plushie.rpgeconomy.common.currency.serialize;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpgeconomy.common.currency.Currency;
import moe.plushie.rpgeconomy.common.currency.Currency.CurrencyVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public final class CurrencySerializer {

    private static final String PROP_NAME = "name";
    private static final String PROP_HAS_WALLET = "hasWallet";
    private static final String PROP_NEED_ITEM_TO_OPEN = "needItemToOpen";
    private static final String PROP_OPEN_WITH_KEYBIND = "opensWithKeybind";
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
        jsonObject.addProperty(PROP_NEED_ITEM_TO_OPEN, currency.getNeedItemToOpen());
        jsonObject.addProperty(PROP_OPEN_WITH_KEYBIND, currency.getOpensWithKeybind());

        JsonArray jsonVariants = new JsonArray();
        CurrencyVariant[] variants = currency.getCurrencyVariants();
        for (int i = 0; i < variants.length; i++) {
            JsonObject jsonVariant = new JsonObject();
            jsonVariant.addProperty(PROP_VAR_NAME, variants[i].getName());
            jsonVariant.addProperty(PROP_VAR_VALUE, variants[i].getValue());
            NBTTagCompound compound = new NBTTagCompound();
            variants[i].getItem().writeToNBT(compound);
            jsonVariant.addProperty(PROP_VAR_ITEM, compound.toString());
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
            JsonElement propPpensWithKeybind = jsonObject.get(PROP_OPEN_WITH_KEYBIND);

            String name = propName.getAsString();
            boolean hasWallet = propHasWallet.getAsBoolean();
            boolean needItemToOpen = propNeedItemToOpen.getAsBoolean();
            boolean opensWithKeybind = propPpensWithKeybind.getAsBoolean();

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
                NBTTagCompound compound = JsonToNBT.getTagFromJson(propVariantItem.getAsJsonObject().toString());
                if (!compound.hasKey("Count")) {
                    compound.setByte("Count", (byte) 1);
                }
                ItemStack item = new ItemStack(compound);

                variants.add(new CurrencyVariant(variantName, variantValue, item));
            }
            return new Currency(name, hasWallet, needItemToOpen, opensWithKeybind, variants.toArray(new CurrencyVariant[variants.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
