package moe.plushie.rpg_framework.core.common.utils;

import com.google.gson.JsonElement;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public final class ItemStackUtils {

    private ItemStackUtils() {
        throw new IllegalAccessError("Utility class.");
    }

    public static void prepareItemStack(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            if (!itemStack.hasTagCompound()) {
                itemStack.setTagCompound(new NBTTagCompound());
            }
        }
    }

    public static void setTagOnItemStack(ItemStack itemStack, NBTBase tag, String key) {
        prepareItemStack(itemStack);
        if (!itemStack.isEmpty()) {
            itemStack.getTagCompound().setTag(key, tag);
        }
    }

    public static boolean isTagOnItemStack(ItemStack itemStack, int type, String key) {
        return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(key, type);
    }

    public static void setItemStackOnItemStack(ItemStack itemStack1, ItemStack itemStack2, String key) {
        if (!itemStack1.isEmpty() & !itemStack2.isEmpty()) {
            JsonElement jsonItem = SerializeHelper.writeItemToJson(itemStack2, true);
            setTagOnItemStack(itemStack1, new NBTTagString(jsonItem.toString()), key);
        }
    }

    public static ItemStack getItemStackFromItemStack(ItemStack itemStack, String key) {
        ItemStack returnStack = ItemStack.EMPTY;
        if (isTagOnItemStack(itemStack, NBT.TAG_STRING, key)) {
            JsonElement jsonItem = SerializeHelper.stringToJson(itemStack.getTagCompound().getString(key));
            returnStack = SerializeHelper.readItemFromJson(jsonItem);
        }
        return returnStack;
    }
}
