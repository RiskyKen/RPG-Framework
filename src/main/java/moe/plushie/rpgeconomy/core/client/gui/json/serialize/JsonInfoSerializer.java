package moe.plushie.rpgeconomy.core.client.gui.json.serialize;

import java.util.HashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpgeconomy.core.client.gui.json.GuiJsonInfo;
import moe.plushie.rpgeconomy.core.client.gui.json.GuiJsonInfo.GuiJsonStyle;
import net.minecraft.util.ResourceLocation;

public final class JsonInfoSerializer {
    
    private static final String PROP_STYLE = "style";
    private static final String PROP_STYLE_COLOUR = "color";
    
    private static final String PROP_TEXTURES = "textures";
    private static final String PROP_TEXTURES_BACKGROUND = "background";
    
    private static final String PROP_CONTROLS = "controls";
    
    private static final String PROP_LAYOUT = "layout";
    private static final String PROP_LAYOUT_WIDTH = "width";
    private static final String PROP_LAYOUT_HEIGHT = "height";
    
    private JsonInfoSerializer() {
    }
    
    public static JsonElement serializeJson(GuiJsonInfo currency) {
        JsonObject jsonObject = new JsonObject();
        /*
        jsonObject.addProperty(PROP_NAME, currency.getName());
        jsonObject.addProperty(PROP_DISPLAY_FORMAT, currency.getDisplayFormat());
        jsonObject.add(PROP_WALLET, serializeCurrencyWalletInfo(currency.getCurrencyWalletInfo()));

        JsonArray jsonVariants = new JsonArray();
        CurrencyVariant[] variants = currency.getCurrencyVariants();
        for (int i = 0; i < variants.length; i++) {
            jsonVariants.add(serializeCurrencyVariant(variants[i]));
        }
        jsonObject.add(PROP_VARIANTS, jsonVariants);*/
        return jsonObject;
    }

    public static GuiJsonInfo deserializeJson(JsonElement json) {
        try {
            JsonObject jsonGuiInfo = json.getAsJsonObject();
            
            JsonObject jsonStyle = jsonGuiInfo.get(PROP_STYLE).getAsJsonObject();
            JsonObject jsonTextures = jsonGuiInfo.get(PROP_TEXTURES).getAsJsonObject();
            JsonObject jsonControls = jsonGuiInfo.get(PROP_CONTROLS).getAsJsonObject();
            JsonObject jsonLayout = jsonGuiInfo.get(PROP_LAYOUT).getAsJsonObject();

            GuiJsonStyle style = new GuiJsonStyle();
            HashMap<String, ResourceLocation> textureMap = new HashMap<String, ResourceLocation>(); 
            /*
            String name = jsonCurrency.get(PROP_NAME).getAsString();
            String displayFormat = jsonCurrency.get(PROP_DISPLAY_FORMAT).getAsString();
            CurrencyWalletInfo walletInfo = deserializeCurrencyWalletInfo(jsonCurrency.get(PROP_WALLET).getAsJsonObject());
            JsonArray jsonVariants = jsonCurrency.get(PROP_VARIANTS).getAsJsonArray();
            ArrayList<CurrencyVariant> variants = new ArrayList<CurrencyVariant>();
            for (int i = 0; i < jsonVariants.size(); i++) {
                variants.add(deserializeCurrencyVariant(jsonVariants.get(i).getAsJsonObject()));
            }*/
            return new GuiJsonInfo(style, textureMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
