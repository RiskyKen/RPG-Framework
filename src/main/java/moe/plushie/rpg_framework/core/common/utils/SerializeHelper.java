package moe.plushie.rpg_framework.core.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.rpg_framework.core.RPGFramework;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants.NBT;

public final class SerializeHelper {

    private static final String TAG_COMPOUND = "compound";
    private static final String TAG_SLOT = "slot";

    private SerializeHelper() {
    }

    public static String readFile(File file, Charset encoding) {
        InputStream inputStream = null;
        String text = null;
        try {
            inputStream = new FileInputStream(file);
            char[] data = IOUtils.toCharArray(inputStream, encoding);
            text = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return text;
    }

    public static JsonElement readJsonFile(File file) {
        return readJsonFile(file, Charsets.UTF_8);
    }

    public static JsonElement readJsonFile(File file, Charset encoding) {
        return stringToJson(readFile(file, encoding));
    }

    public static void writeFile(File file, Charset encoding, String text) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, false);
            byte[] data = text.getBytes(encoding);
            outputStream.write(data);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    public static void writeJsonFile(File file, Charset encoding, JsonElement json) {
        writeFile(file, encoding, json.toString());
    }

    public static void writeJsonFile(JsonElement json, File file) {
        writeFile(file, Charsets.UTF_8, json.toString());
    }

    public static JsonElement stringToJson(String jsonString) {
        try {
            JsonParser parser = new JsonParser();
            return parser.parse(jsonString);
        } catch (Exception e) {
            RPGFramework.getLogger().error("Error parsing json.");
            RPGFramework.getLogger().error(e.getLocalizedMessage());
            return null;
        }
    }

    public static JsonArray writeItemsToJson(NonNullList<ItemStack> items, boolean compact) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < items.size(); i++) {
            JsonObject jsonObject = writeItemToJson(items.get(i), compact);
            jsonObject.addProperty(TAG_SLOT, i);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public static NonNullList<ItemStack> readItemsFromJson(JsonArray jsonArray) {
        NonNullList<ItemStack> items = NonNullList.<ItemStack>withSize(jsonArray.size(), ItemStack.EMPTY);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            ItemStack itemStack = readItemFromJson(jsonObject);
            int slot = jsonObject.get(TAG_SLOT).getAsInt();
            items.set(slot, itemStack);
        }
        return items;
    }

    public static JsonObject writeItemToJson(ItemStack itemStack, boolean compact) {
        JsonObject jsonObject = new JsonObject();
        if (itemStack.isEmpty()) {
            return jsonObject;
        }

        if (compact) {
            jsonObject.addProperty("id", String.valueOf(Item.getIdFromItem(itemStack.getItem())));
        } else {
            jsonObject.addProperty("id", itemStack.getItem().getRegistryName().toString());
        }
        jsonObject.addProperty("count", itemStack.getCount());
        jsonObject.addProperty("damage", itemStack.getItemDamage());
        if (itemStack.hasTagCompound()) {
            jsonObject.addProperty("nbt", itemStack.getTagCompound().toString());
        }

        NBTTagCompound compound = itemStack.writeToNBT(new NBTTagCompound());
        if (compound.hasKey("ForgeCaps", NBT.TAG_COMPOUND)) {
            jsonObject.addProperty("capabilities", compound.getCompoundTag("ForgeCaps").toString());
        }

        return jsonObject;
    }

    public static ItemStack readItemFromJson(JsonElement jsonElement) {
        return readItemFromJson(jsonElement.getAsJsonObject());
    }

    public static ItemStack readItemFromJson(JsonObject jsonObject) {
        ItemStack itemStack = ItemStack.EMPTY;
        try {
            if (jsonObject.has(TAG_COMPOUND)) {
                NBTTagCompound compound = JsonToNBT.getTagFromJson(jsonObject.get(TAG_COMPOUND).getAsString());
                return new ItemStack(compound);
            }
            if (!jsonObject.has("id")) {
                return ItemStack.EMPTY;
            }
            Item item = Item.getByNameOrId(jsonObject.get("id").getAsString());
            int count = 1;
            int damage = 0;
            NBTTagCompound capabilities = null;
            if (jsonObject.has("count")) {
                count = jsonObject.get("count").getAsInt();
            }
            if (jsonObject.has("Count")) {
                count = jsonObject.get("Count").getAsInt();
            }
            if (jsonObject.has("damage")) {
                damage = jsonObject.get("damage").getAsInt();
            }
            if (jsonObject.has("Damage")) {
                damage = jsonObject.get("Damage").getAsInt();
            }
            if (jsonObject.has("capabilities")) {
                capabilities = JsonToNBT.getTagFromJson(jsonObject.get("capabilities").getAsString());
            }
            itemStack = new ItemStack(item, count, damage, capabilities);
            if (jsonObject.has("nbt")) {
                JsonElement elementNbt = jsonObject.get("nbt");
                NBTTagCompound nbtBase = JsonToNBT.getTagFromJson(elementNbt.getAsString());
                itemStack.setTagCompound(nbtBase);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemStack;
    }
}
