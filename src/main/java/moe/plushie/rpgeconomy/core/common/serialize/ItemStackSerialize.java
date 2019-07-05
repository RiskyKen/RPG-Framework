package moe.plushie.rpgeconomy.core.common.serialize;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;

public class ItemStackSerialize implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return SerializeHelper.readItemFromJson(json);
        } catch (NBTException e) {
            e.printStackTrace();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        return SerializeHelper.writeItemToJson(src, false);
    }
}
