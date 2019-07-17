package moe.plushie.rpg_framework.core.client.gui.json;

import java.util.HashMap;

import net.minecraft.util.ResourceLocation;

public class GuiJsonInfo {

    private final GuiJsonStyle style;
    private final HashMap<String, ResourceLocation> textureMap;
    
    public GuiJsonInfo(GuiJsonStyle style, HashMap<String, ResourceLocation> textureMap) {
        this.style = style;
        this.textureMap = textureMap;
    }

    public static class GuiJsonStyle {
        
    }
}
