package moe.plushie.rpgeconomy;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import moe.plushie.rpgeconomy.common.creativetab.CreativeTabRPGEconomy;
import moe.plushie.rpgeconomy.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.proxies.CommonProxy;

@Mod(modid = LibModInfo.ID, version = LibModInfo.VERSION)
public class RPGEconomy {
    
    @Instance(LibModInfo.ID)
    private static RPGEconomy instance;

    @SidedProxy(clientSide = LibModInfo.PROXY_CLIENT_CLASS, serverSide = LibModInfo.PROXY_COMMNON_CLASS)
    private static CommonProxy proxy;
    
    private static Logger logger;
    
    private static final CreativeTabRPGEconomy creativeTabRPGEconomy = new CreativeTabRPGEconomy();
    
    @EventHandler
    public void perInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info(String.format("Loading %s version %s", LibModInfo.NAME, LibModInfo.VERSION));
        proxy.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        proxy.initRenderers();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
    
    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
    }
    
    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
    }
    
    public static CommonProxy getProxy() {
        return proxy;
    }
    
    public static RPGEconomy getInstance() {
        return instance;
    }
    
    public static Logger getLogger() {
        return logger;
    }
    
    public static CreativeTabRPGEconomy getCreativetabrpgeconomy() {
        return creativeTabRPGEconomy;
    }
}
