package moe.plushie.rpgeconomy.loot.common;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import moe.plushie.rpgeconomy.api.loot.ILootTableItem;

public class LootTableItemSerializer implements JsonSerializer<ILootTableItem>, JsonDeserializer<ILootTableItem> {

    @Override
    public ILootTableItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, LootTableItem.class);
    }

    @Override
    public JsonElement serialize(ILootTableItem src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src, LootTableItem.class);
    }
}
