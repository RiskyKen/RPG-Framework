package moe.plushie.rpg_framework.core.common.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.core.common.ItemMatcherStack;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import net.minecraft.item.ItemStack;

public final class ItemMacherSerializer {

    public static final String PROP_ITEM_MATCHER = "itemMatcher";

    private static final String PROP_TYPE_STACK = "stack";
    private static final String PROP_ITEM = "item";
    private static final String PROP_MATCH_META = "matchMeta";
    private static final String PROP_MATCH_NBT = "matchNBT";

    private ItemMacherSerializer() {
    }

    public static JsonObject serializeJson(IItemMatcher matcher, boolean compact) {
        JsonObject jsonObject = new JsonObject();

        if (matcher instanceof ItemMatcherStack) {
            ItemMatcherStack matcherStack = (ItemMatcherStack) matcher;
            JsonObject jsonStack = new JsonObject();
            jsonStack.add(PROP_ITEM, SerializeHelper.writeItemToJson(matcher.getItemStack(), compact));
            jsonStack.addProperty(PROP_MATCH_META, matcherStack.isMatchMeta());
            jsonStack.addProperty(PROP_MATCH_NBT, matcherStack.isMatchNBT());
            jsonObject.add(PROP_TYPE_STACK, jsonStack);
        }

        return jsonObject;
    }

    public static IItemMatcher deserializeJson(JsonElement jsonElement) {
        try {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            IItemMatcher matcher = null;

            if (jsonObject.has(PROP_TYPE_STACK)) {
                ItemStack itemStack = ItemStack.EMPTY;
                boolean matchMeta = false;
                boolean matchNBT = false;

                if (jsonObject.has(PROP_TYPE_STACK)) {
                    JsonObject jsonStack = jsonObject.get(PROP_TYPE_STACK).getAsJsonObject();
                    itemStack = SerializeHelper.readItemFromJson(jsonStack.get(PROP_ITEM));
                    if (jsonStack.has(PROP_MATCH_META)) {
                        matchMeta = jsonStack.get(PROP_MATCH_META).getAsBoolean();
                    }
                    if (jsonStack.has(PROP_MATCH_NBT)) {
                        matchNBT = jsonStack.get(PROP_MATCH_NBT).getAsBoolean();
                    }
                }
                matcher = new ItemMatcherStack(itemStack, matchMeta, matchNBT);
            }
            return matcher;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
