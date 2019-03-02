package moe.plushie.rpgeconomy.core.proxies;

import java.io.File;

import moe.plushie.rpgeconomy.auction.ModuleAuction;
import moe.plushie.rpgeconomy.core.common.command.CommandRpg;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.init.ModBlocks;
import moe.plushie.rpgeconomy.core.common.init.ModItems;
import moe.plushie.rpgeconomy.core.common.init.ModSounds;
import moe.plushie.rpgeconomy.core.common.init.ModTiles;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.common.module.IModModule;
import moe.plushie.rpgeconomy.core.common.module.ModModule;
import moe.plushie.rpgeconomy.core.common.network.GuiHandler;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.currency.ModuleCurrency;
import moe.plushie.rpgeconomy.currency.common.CurrencyManager;
import moe.plushie.rpgeconomy.currency.common.capability.CurrencyCapabilityManager;
import moe.plushie.rpgeconomy.mail.ModuleMail;
import moe.plushie.rpgeconomy.mail.common.MailSystemManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public class CommonProxy {
    
    private File modConfigDirectory;
    private File instanceDirectory;
    private File modDirectory;
    
    private ModBlocks modBlocks;
    private ModItems modItems;
    private ModSounds modSounds;
    
    private CurrencyManager currencyManager;
    private MailSystemManager mailSystemManager;
    
    private IModModule moduleCurrency = new ModuleCurrency();
    private IModModule moduleMail = new ModuleMail();
    private IModModule moduleAuction = new ModuleAuction();
    
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
        modSounds = new ModSounds();
        
        CurrencyCapabilityManager.register();
        
        for (IModModule module : ModModule.MOD_MODULES) {
            module.preInit(event);
        }
    }
    
    public void init(FMLInitializationEvent event) {
        ModTiles.registerTileEntities();
        new GuiHandler();
        new PacketHandler();
        
        for (IModModule module : ModModule.MOD_MODULES) {
            module.init(event);
        }
    }
    
    public void initRenderers() {
    }
    
    public void postInit(FMLPostInitializationEvent event) {
        for (IModModule module : ModModule.MOD_MODULES) {
            module.postInit(event);
        }
    }
    
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        for (IModModule module : ModModule.MOD_MODULES) {
            module.serverAboutToStart(event);
        }
    }
    
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandRpg());
        for (IModModule module : ModModule.MOD_MODULES) {
            module.serverStarting(event);
        }
    }
    
    public void serverStopping(FMLServerStoppingEvent event) {
        for (IModModule module : ModModule.MOD_MODULES) {
            module.serverStopping(event);
        }
    }
    
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }
    
    public MailSystemManager getMailSystemManager() {
        return mailSystemManager;
    }
}
