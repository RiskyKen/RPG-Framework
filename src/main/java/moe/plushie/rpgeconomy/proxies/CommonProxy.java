package moe.plushie.rpgeconomy.proxies;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.server.MinecraftServer;

public class CommonProxy {
    
    //private ModBlocks modBlocks;
    //private ModItems modItems;
    
    public void preInit(FMLPreInitializationEvent event) {
        //modBlocks = new ModBlocks();
        //modItems = new ModItems();
    }
    
    public void init(FMLInitializationEvent event) {
    }
    
    public void initRenderers() {}
    
    public void postInit(FMLPostInitializationEvent event) {
    }
    
    public MinecraftServer getServer() {
        return MinecraftServer.getServer();
    }
}
