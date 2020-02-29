package moe.plushie.rpg_framework.core.common.utils;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public final class NBTUtils {

    private NBTUtils() {
        throw new IllegalAccessError("Utility class.");
    }

    public static NBTTagList createListFromArray(String[] array) {
        NBTTagList tagList = new NBTTagList();
        if (array != null & array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                tagList.appendTag(new NBTTagString(array[i]));
            }
        }
        return tagList;
    }

    public static String[] getArrayFromList(NBTTagList tagList) {
        String[] strings = new String[tagList.tagCount()];
        for (int i = 0; i < tagList.tagCount(); i++) {
            strings[i] = tagList.getStringTagAt(i);
        }
        return strings;
    }
}
