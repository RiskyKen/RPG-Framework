package moe.plushie.rpgeconomy.mail.common.serialize;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.mail.common.MailSystem;

public class MailSystemReferenceAdapter implements JsonDeserializer<MailSystem>, JsonSerializer<MailSystem> {

    @Override
    public MailSystem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.getAsJsonObject().has("identifier")) {
            return RpgEconomy.getProxy().getMailSystemManager().getMailSystem(json.getAsJsonObject().get("identifier").getAsString());
        }
        
        //Refra
        
        return null;
    }

    @Override
    public JsonElement serialize(MailSystem src, Type typeOfSrc, JsonSerializationContext context) {
        // TODO Auto-generated method stub
        return null;
    }
}
