package moe.plushie.rpg_economy.proxies;

import moe.plushie.rpg_economy.common.init.ModBlocks;
import moe.plushie.rpg_economy.common.init.ModItems;
import moe.plushie.rpg_economy.common.lib.LibModInfo;
import moe.plushie.rpg_economy.common.mail.MailSystemManager;
import moe.plushie.rpg_economy.common.network.GuiHandler;
import moe.plushie.rpg_economy.common.network.PacketHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public class CommonProxy {
    
    private MailSystemManager mailSystemManager;
    private ModBlocks modBlocks;
    private ModItems modItems;
    
    public void preInit(FMLPreInitializationEvent event) {
        modBlocks = new ModBlocks();
        modItems = new ModItems();
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
    
    public MailSystemManager getMailSystemManager() {
        return mailSystemManager;
    }
}
