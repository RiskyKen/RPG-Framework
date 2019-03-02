package moe.plushie.rpgeconomy.core;

import org.apache.logging.log4j.Logger;

import moe.plushie.rpgeconomy.core.common.creativetab.CreativeTabRPGEconomy;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.proxies.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = LibModInfo.ID, version = LibModInfo.VERSION)
public class RpgEconomy {

    @Instance(LibModInfo.ID)
    private static RpgEconomy instance;

    @SidedProxy(clientSide = LibModInfo.PROXY_CLIENT_CLASS, serverSide = LibModInfo.PROXY_COMMNON_CLASS)
    private static CommonProxy proxy;

    private static Logger logger;

    private static final CreativeTabRPGEconomy CREATIVE_TAB_RPG_ECONOMY = new CreativeTabRPGEconomy();

    @EventHandler
    public void perInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        // reputation system
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
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        proxy.serverAboutToStart(event);
    }
    
    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        proxy.serverStopping(event);
    }

    public static CommonProxy getProxy() {
        return proxy;
    }

    public static RpgEconomy getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static CreativeTabRPGEconomy getCreativeTabRPGEconomy() {
        return CREATIVE_TAB_RPG_ECONOMY;
    }
}
