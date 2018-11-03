package moe.plushie.rpgeconomy.proxies;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import moe.plushie.rpgeconomy.common.blocks.ModBlocks;
import moe.plushie.rpgeconomy.common.mail.MailSystemManager;
import moe.plushie.rpgeconomy.common.network.GuiHandler;
import moe.plushie.rpgeconomy.common.network.PacketHandler;
import net.minecraft.server.MinecraftServer;

public class CommonProxy {
    
    private MailSystemManager mailSystemManager;
    private ModBlocks modBlocks;
    // private ModItems modItems;
    
    public void preInit(FMLPreInitializationEvent event) {
        modBlocks = new ModBlocks();
        // modItems = new ModItems();
    }
    
    public void init(FMLInitializationEvent event) {
        modBlocks.registerTileEntities();
        new GuiHandler();
        new PacketHandler();
        mailSystemManager = new MailSystemManager();
    }
    
    public void initRenderers() {
    }
    
    public void postInit(FMLPostInitializationEvent event) {
    }
    
    public MinecraftServer getServer() {
        return MinecraftServer.getServer();
    }
    
    public MailSystemManager getMailSystemManager() {
        return mailSystemManager;
    }
}
