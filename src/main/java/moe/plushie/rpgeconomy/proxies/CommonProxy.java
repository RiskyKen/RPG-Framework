package moe.plushie.rpgeconomy.proxies;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import moe.plushie.rpgeconomy.common.blocks.ModBlocks;
import moe.plushie.rpgeconomy.common.mail.MailManager;
import net.minecraft.server.MinecraftServer;

public class CommonProxy {
    
    private MailManager mailManager;
    private ModBlocks modBlocks;
    //private ModItems modItems;
    
    public void preInit(FMLPreInitializationEvent event) {
        modBlocks = new ModBlocks();
        //modItems = new ModItems();
    }
    
    public void init(FMLInitializationEvent event) {
        mailManager = new MailManager();
    }
    
    public void initRenderers() {}
    
    public void postInit(FMLPostInitializationEvent event) {
    }
    
    public MinecraftServer getServer() {
        return MinecraftServer.getServer();
    }
    
    public MailManager getMailManager() {
        return mailManager;
    }
}
