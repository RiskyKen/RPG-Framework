package moe.plushie.rpgeconomy.common.mail.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpgeconomy.common.mail.MailSystem;

public class MailSystemSerializer {
    
    private static final String PROP_NAME = "name";
    
    private MailSystemSerializer() {}
    
    public static JsonElement serialize(MailSystem mailSystem) {
        if (mailSystem == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        //jsonObject.addProperty("name", src.getName());
        return jsonObject;
    }
    
    public static MailSystem deserializeJson(JsonElement json) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            JsonElement propName = jsonObject.get(PROP_NAME);

            String name = propName.getAsString();
            return new MailSystem(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
