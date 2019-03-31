package moe.plushie.rpgeconomy.core.client.gui;

import java.util.HashMap;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.client.gui.json.GuiJsonInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiResourceManager implements IResourceManagerReloadListener {

	private static final HashMap<ResourceLocation, GuiJsonInfo> GUI_RESOURCE_MAP = new HashMap<ResourceLocation, GuiJsonInfo>();
	
	public GuiResourceManager() {
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        if (resourceManager instanceof SimpleReloadableResourceManager) {
        	((SimpleReloadableResourceManager)resourceManager).registerReloadListener(this);
        }
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
	    RpgEconomy.getLogger().info("Loading GUI resources.");
	    synchronized (GUI_RESOURCE_MAP) {
	        GUI_RESOURCE_MAP.clear();
        }
	}
	
	public static GuiJsonInfo getGuiJsonInfo(ResourceLocation resourceLocation) {
	    synchronized (GUI_RESOURCE_MAP) {
	        if (!GUI_RESOURCE_MAP.containsKey(resourceLocation)) {
	            GUI_RESOURCE_MAP.put(resourceLocation, loadGuiJsonInfo(resourceLocation));
	        }
	        return GUI_RESOURCE_MAP.get(resourceLocation);
        }
    }
	
	private static GuiJsonInfo loadGuiJsonInfo(ResourceLocation resourceLocation) {
	    
	    //SerializeHelper.readJsonFile(file)
	    return null;
	}
}
