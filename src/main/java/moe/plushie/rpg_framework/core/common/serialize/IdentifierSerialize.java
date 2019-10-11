package moe.plushie.rpg_framework.core.common.serialize;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.core.common.IdentifierInt;
import moe.plushie.rpg_framework.core.common.IdentifierString;

public class IdentifierSerialize implements JsonSerializer<IIdentifier>, JsonDeserializer<IIdentifier> {

    private static final String PROP_TYPE = "type";
    private static final String PROP_VALUE = "value";

    private static final String PROP_TYPE_STRING = "string";
    private static final String PROP_TYPE_INT = "int";

    public static JsonObject serializeJson(IIdentifier identifier) {
        JsonObject jsonObject = new JsonObject();

        if (identifier instanceof IdentifierString) {
            IdentifierString identifierString = (IdentifierString) identifier;
            jsonObject.addProperty(PROP_TYPE, PROP_TYPE_STRING);
            jsonObject.addProperty(PROP_VALUE, identifierString.getValue());
        }

        if (identifier instanceof IdentifierInt) {
            IdentifierInt identifierInt = (IdentifierInt) identifier;
            jsonObject.addProperty(PROP_TYPE, PROP_TYPE_INT);
            jsonObject.addProperty(PROP_VALUE, identifierInt.getValue());
        }

        return jsonObject;
    }

    public static IIdentifier deserializeJson(JsonElement jsonElement) {
        try {
            return deserializeJson(jsonElement.getAsJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IIdentifier deserializeJson(JsonObject jsonObject) {
        try {
            IIdentifier identifier = null;

            String type = jsonObject.get(PROP_TYPE).getAsString();
            JsonElement value = jsonObject.get(PROP_VALUE);

            if (type.equals(PROP_TYPE_STRING)) {
                identifier = new IdentifierString(value.getAsString());
            } else if (type.equals(PROP_TYPE_INT)) {
                identifier = new IdentifierInt(value.getAsInt());
            }

            return identifier;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public IIdentifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserializeJson(json);
    }

    @Override
    public JsonElement serialize(IIdentifier src, Type typeOfSrc, JsonSerializationContext context) {
        return serializeJson(src);
    }
}
