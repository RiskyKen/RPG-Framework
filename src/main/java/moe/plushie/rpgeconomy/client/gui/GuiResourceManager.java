package moe.plushie.rpgeconomy.client.gui;

import moe.plushie.rpgeconomy.RpgEconomy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiResourceManager implements IResourceManagerReloadListener {

	//private static final HashMap<ResourceLocation, IJsonGui> GUI_RESOURCE_MAP = new HashMap<ResourceLocation, IJsonGui>();
	
	public GuiResourceManager() {
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        if (resourceManager instanceof SimpleReloadableResourceManager) {
        	((SimpleReloadableResourceManager)resourceManager).registerReloadListener(this);
        }
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		RpgEconomy.getLogger().info("Loading GUI resources.");
	}
	
	/*public static interface IJsonGui {
		
	}*/
}
