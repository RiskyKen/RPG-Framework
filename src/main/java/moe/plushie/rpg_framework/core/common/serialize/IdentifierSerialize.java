package moe.plushie.rpg_framework.core.common.serialize;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
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

    public static JsonPrimitive serializeJson(IIdentifier identifier) {
        JsonPrimitive jsonPrimitive = new JsonPrimitive("");

        if (identifier instanceof IdentifierString) {
            IdentifierString identifierString = (IdentifierString) identifier;
            jsonPrimitive = new JsonPrimitive(identifierString.getValue());
        }

        if (identifier instanceof IdentifierInt) {
            IdentifierInt identifierInt = (IdentifierInt) identifier;
            jsonPrimitive = new JsonPrimitive(identifierInt.getValue());
        }

        return jsonPrimitive;
    }

    public static IIdentifier deserializeJson(JsonElement jsonElement) {
        try {
            IIdentifier identifier = null;

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String type = jsonObject.get(PROP_TYPE).getAsString();
                JsonElement value = jsonObject.get(PROP_VALUE);

                if (type.equals(PROP_TYPE_STRING)) {
                    identifier = new IdentifierString(value.getAsString());
                } else if (type.equals(PROP_TYPE_INT)) {
                    identifier = new IdentifierInt(value.getAsInt());
                }
            }
            if (jsonElement.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if (jsonPrimitive.isNumber()) {
                    identifier = new IdentifierInt(jsonPrimitive.getAsInt());
                } else if (jsonPrimitive.isString()) {
                    identifier = new IdentifierString(jsonPrimitive.getAsString());
                }
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
