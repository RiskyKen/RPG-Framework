package moe.plushie.rpgeconomy.proxies;

import java.io.File;

import moe.plushie.rpgeconomy.common.capability.ModCapabilityManager;
import moe.plushie.rpgeconomy.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.common.currency.CurrencyManager;
import moe.plushie.rpgeconomy.common.init.ModBlocks;
import moe.plushie.rpgeconomy.common.init.ModItems;
import moe.plushie.rpgeconomy.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.common.mail.MailSystemManager;
import moe.plushie.rpgeconomy.common.network.GuiHandler;
import moe.plushie.rpgeconomy.common.network.PacketHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public class CommonProxy {
    
    private File modConfigDirectory;
    private File instanceDirectory;
    private File modDirectory;
    
    private ModBlocks modBlocks;
    private ModItems modItems;
    
    private CurrencyManager currencyManager;
    private MailSystemManager mailSystemManager;
    
    public void preInit(FMLPreInitializationEvent event) {
        modConfigDirectory = new File(event.getSuggestedConfigurationFile().getParentFile(), LibModInfo.ID);
        if (!modConfigDirectory.exists()) {
            modConfigDirectory.mkdir();
        }
        ConfigHandler.init(new File(modConfigDirectory, "common.cfg"));
        instanceDirectory = event.getSuggestedConfigurationFile().getParentFile().getParentFile();
        modDirectory = new File(instanceDirectory, LibModInfo.ID);
        if (!modDirectory.exists()) {
            modDirectory.mkdir();
        }
        
        currencyManager = new CurrencyManager(modDirectory);
        mailSystemManager = new MailSystemManager(modDirectory);
        
        modBlocks = new ModBlocks();
        modItems = new ModItems();
        ModCapabilityManager.register();
    }
    
    public void init(FMLInitializationEvent event) {
        modBlocks.registerTileEntities();
        new GuiHandler();
        new PacketHandler();
        
        currencyManager.reload(false);
        mailSystemManager.reload(false);
    }
    
    public void initRenderers() {
    }
    
    public void postInit(FMLPostInitializationEvent event) {
    }
    
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }
    
    public MailSystemManager getMailSystemManager() {
        return mailSystemManager;
    }
}
