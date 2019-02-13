package moe.plushie.rpgeconomy.common.currency.serialize;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpgeconomy.common.currency.Currency;
import moe.plushie.rpgeconomy.common.currency.Currency.Variant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public final class CurrencySerializer {
    
    private CurrencySerializer() {
    }
    
    /*public static JsonElement serialize(Currency currency) {
        
    }*/
    
    public static Currency deserialize(JsonElement json) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement elementName = jsonObject.get("name");
            JsonElement elementShowsInWallet = jsonObject.get("shows in wallet");
            JsonElement elementVariants = jsonObject.get("variants");
            
            String name = elementName.getAsString();
            boolean showsInWallet = elementShowsInWallet.getAsBoolean();
            JsonArray jsonArray = elementVariants.getAsJsonArray();
            
            ArrayList<Variant> variants = new ArrayList<Variant>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonVariant = jsonArray.get(i).getAsJsonObject();
                JsonElement elementVariantName = jsonVariant.get("name");
                JsonElement elementVariantValue = jsonVariant.get("value");
                
                String variantName = elementVariantName.getAsString();
                int variantValue = elementVariantValue.getAsInt();
                ItemStack[] items = null;
                
                if (jsonVariant.has("items")) {
                    JsonElement variantItems = jsonVariant.get("items");
                    JsonArray variantArray = variantItems.getAsJsonArray();
                    items = new ItemStack[variantArray.size()];
                    for (int j = 0; j < variantArray.size(); j++) {
                        NBTTagCompound nbtBase = JsonToNBT.getTagFromJson(variantArray.get(j).toString());
                        if (!nbtBase.hasKey("Count")) {
                            nbtBase.setByte("Count", (byte) 1);
                        }
                        items[j] = new ItemStack(nbtBase);
                    }
                }
                variants.add(new Variant(variantName, variantValue, items));
            }
            return new Currency(name, showsInWallet, variants.toArray(new Variant[variants.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
