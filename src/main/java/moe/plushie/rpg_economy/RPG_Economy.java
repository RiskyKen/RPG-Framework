package moe.plushie.rpg_economy;

import org.apache.logging.log4j.Logger;

import moe.plushie.rpg_economy.common.creativetab.CreativeTabRPGEconomy;
import moe.plushie.rpg_economy.common.lib.LibModInfo;
import moe.plushie.rpg_economy.proxies.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = LibModInfo.ID, version = LibModInfo.VERSION)
public class RPG_Economy {
    
    @Instance(LibModInfo.ID)
    private static RPG_Economy instance;

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
    
    public static RPG_Economy getInstance() {
        return instance;
    }
    
    public static Logger getLogger() {
        return logger;
    }
    
    public static CreativeTabRPGEconomy getCreativeTabRPGEconomy() {
        return creativeTabRPGEconomy;
    }
}
