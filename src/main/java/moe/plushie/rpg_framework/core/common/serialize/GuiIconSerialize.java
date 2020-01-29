package moe.plushie.rpg_framework.core.common.serialize;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import moe.plushie.rpg_framework.api.core.IGuiIcon;
import moe.plushie.rpg_framework.api.core.IGuiIcon.AnchorHorizontal;
import moe.plushie.rpg_framework.api.core.IGuiIcon.AnchorVertical;
import moe.plushie.rpg_framework.core.common.GuiIcon;

public class GuiIconSerialize implements JsonSerializer<IGuiIcon>, JsonDeserializer<IGuiIcon> {

    private static final String PROP_GUI_CLASS_PATHS = "gui_class_paths";
    private static final String PROP_ANCHOR_HORIZONTAL = "anchor_horizontal";
    private static final String PROP_ANCHOR_VERTICAL = "anchor_vertical";
    private static final String PROP_OFFSET_HORIZONTAL = "offset_horizontal";
    private static final String PROP_OFFSET_VERTICAL = "offset_vertical";
    private static final String PROP_ICON_INDEX = "icon_index";
    private static final String PROP_ICON_ALPHA = "icon_alpha";

    public static JsonObject serializeJson(IGuiIcon guiIcon) {
        JsonObject jsonObject = new JsonObject();

        JsonArray jsonArray = new JsonArray();
        for (String classPath : guiIcon.getClassPaths()) {
            jsonArray.add(classPath);
        }
        jsonObject.add(PROP_GUI_CLASS_PATHS, jsonArray);
        jsonObject.addProperty(PROP_ANCHOR_HORIZONTAL, guiIcon.getAnchorHorizontal().toString().toLowerCase());
        jsonObject.addProperty(PROP_ANCHOR_VERTICAL, guiIcon.getAnchorVertical().toString().toLowerCase());
        jsonObject.addProperty(PROP_OFFSET_HORIZONTAL, guiIcon.getOffsetHorizontal());
        jsonObject.addProperty(PROP_OFFSET_VERTICAL, guiIcon.getOffsetVertical());
        jsonObject.addProperty(PROP_ICON_INDEX, guiIcon.getIconIndex());
        jsonObject.addProperty(PROP_ICON_ALPHA, guiIcon.getIconAlpha());

        return jsonObject;
    }

    public static IGuiIcon deserializeJson(JsonElement jsonElement) {
        try {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String[] classPaths = new String[] {};
            AnchorHorizontal anchorHorizontal = AnchorHorizontal.RIGHT;
            AnchorVertical anchorVertical = AnchorVertical.TOP;
            int offsetHorizontal = -5;
            int offsetVertical = 5;
            int iconIndex = 19;
            float iconAlpha = 0.75F;

            if (jsonObject.has(PROP_GUI_CLASS_PATHS)) {
                JsonArray jsonArray = jsonObject.get(PROP_GUI_CLASS_PATHS).getAsJsonArray();
                classPaths = new String[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++) {
                    classPaths[i] = jsonArray.get(i).getAsString();
                }
            }
            if (jsonObject.has(PROP_ANCHOR_HORIZONTAL)) {
                anchorHorizontal = AnchorHorizontal.valueOf(jsonObject.get(PROP_ANCHOR_HORIZONTAL).getAsString().toUpperCase());
            }
            if (jsonObject.has(PROP_ANCHOR_VERTICAL)) {
                anchorVertical = AnchorVertical.valueOf(jsonObject.get(PROP_ANCHOR_VERTICAL).getAsString().toUpperCase());
            }
            if (jsonObject.has(PROP_OFFSET_HORIZONTAL)) {
                offsetHorizontal = jsonObject.get(PROP_OFFSET_HORIZONTAL).getAsInt();
            }
            if (jsonObject.has(PROP_OFFSET_VERTICAL)) {
                offsetVertical = jsonObject.get(PROP_OFFSET_VERTICAL).getAsInt();
            }
            if (jsonObject.has(PROP_ICON_INDEX)) {
                iconIndex = jsonObject.get(PROP_ICON_INDEX).getAsInt();
            }
            if (jsonObject.has(PROP_ICON_ALPHA)) {
                iconAlpha = jsonObject.get(PROP_ICON_ALPHA).getAsFloat();
            }

            return new GuiIcon(classPaths, anchorHorizontal, anchorVertical, offsetHorizontal, offsetVertical, iconIndex, iconAlpha);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public IGuiIcon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserializeJson(json);
    }

    @Override
    public JsonElement serialize(IGuiIcon src, Type typeOfSrc, JsonSerializationContext context) {
        return serializeJson(src);
    }
}
