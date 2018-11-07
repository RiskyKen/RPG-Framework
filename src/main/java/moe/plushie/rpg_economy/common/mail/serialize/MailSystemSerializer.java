package moe.plushie.rpg_economy.common.mail.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.rpg_economy.RPG_Economy;
import moe.plushie.rpg_economy.common.mail.MailSystem;

public class MailSystemSerializer {
    
    private MailSystemSerializer() {}
    
    public static JsonElement serialize(MailSystem mailSystem) {
        if (mailSystem == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        //jsonObject.addProperty("name", src.getName());
        return jsonObject;
    }
    
    public static MailSystem deserialize(String jsonString) {
        if (jsonString == null) {
            return null;
        }
        try {
            JsonParser parser = new JsonParser();
            return deserialize(parser.parse(jsonString)); 
        } catch (Exception e) {
            RPG_Economy.getLogger().error("Error loading mail message.");
            RPG_Economy.getLogger().error(e.getLocalizedMessage());
            return null;
        }
    }
    
    public static MailSystem deserialize(JsonElement json) {
        return null;
    }
}
