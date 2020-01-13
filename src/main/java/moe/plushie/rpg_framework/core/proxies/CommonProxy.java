package moe.plushie.rpg_framework.core.proxies;

import java.io.File;

import moe.plushie.rpg_framework.api.RpgEconomyAPI;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.auction.ModuleAuction;
import moe.plushie.rpg_framework.bank.ModuleBank;
import moe.plushie.rpg_framework.bank.common.BankManager;
import moe.plushie.rpg_framework.bank.common.capability.BankCapabilityManager;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.addons.ModAddonManager;
import moe.plushie.rpg_framework.core.common.command.CommandRpg;
import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.init.ModBlocks;
import moe.plushie.rpg_framework.core.common.init.ModItems;
import moe.plushie.rpg_framework.core.common.init.ModSounds;
import moe.plushie.rpg_framework.core.common.init.ModTiles;
import moe.plushie.rpg_framework.core.common.lib.EnumGuiId;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.common.lib.LibModKeys.ModKey;
import moe.plushie.rpg_framework.core.common.module.IModModule;
import moe.plushie.rpg_framework.core.common.module.ModModule;
import moe.plushie.rpg_framework.core.common.network.GuiHandler;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.currency.ModuleCurrency;
import moe.plushie.rpg_framework.currency.common.Currency;
import moe.plushie.rpg_framework.currency.common.CurrencyManager;
import moe.plushie.rpg_framework.currency.common.capability.CurrencyCapabilityManager;
import moe.plushie.rpg_framework.mail.ModuleMail;
import moe.plushie.rpg_framework.mail.common.MailSystemManager;
import moe.plushie.rpg_framework.shop.ModuleShop;
import moe.plushie.rpg_framework.shop.common.ShopManager;
import moe.plushie.rpg_framework.value.ModuleValue;
import moe.plushie.rpg_framework.value.ValueManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public class CommonProxy {

    private File modConfigDirectory;
    private File configDirectory;
    private File instanceDirectory;
    private File modDirectory;

    private ModBlocks modBlocks;
    private ModItems modItems;
    private ModSounds modSounds;

    private CurrencyManager currencyManager;
    private ValueManager valueManager;
    private MailSystemManager mailSystemManager;
    private ShopManager shopManager;
    private BankManager bankManager;

    private IModModule moduleCurrency = new ModuleCurrency();
    private IModModule moduleValue = new ModuleValue(getModDirectory());
    private IModModule moduleMail = new ModuleMail();
    private IModModule moduleShop = new ModuleShop();
    private IModModule moduleBank = new ModuleBank();
    private IModModule moduleAuction = new ModuleAuction();

    public void preInit(FMLPreInitializationEvent event) {
        modConfigDirectory = new File(event.getSuggestedConfigurationFile().getParentFile(), LibModInfo.ID);
        if (!modConfigDirectory.exists()) {
            modConfigDirectory.mkdir();
        }
        ConfigHandler.init(new File(modConfigDirectory, "common.cfg"));
        configDirectory = event.getSuggestedConfigurationFile().getParentFile();
        instanceDirectory = configDirectory.getParentFile();
        modDirectory = new File(instanceDirectory, LibModInfo.ID);
        if (!modDirectory.exists()) {
            modDirectory.mkdir();
        }
        
        ModAddonManager.preInit();

        currencyManager = new CurrencyManager(modDirectory);
        mailSystemManager = new MailSystemManager(modDirectory);
        shopManager = new ShopManager(modDirectory);
        bankManager = new BankManager(modDirectory);

        ReflectionHelper.setPrivateValue(RpgEconomyAPI.class, null, currencyManager, "currencyManager");
        ReflectionHelper.setPrivateValue(RpgEconomyAPI.class, null, mailSystemManager, "mailSystemManager");
        ReflectionHelper.setPrivateValue(RpgEconomyAPI.class, null, shopManager, "shopManager");
        ReflectionHelper.setPrivateValue(RpgEconomyAPI.class, null, bankManager, "bankManager");

        modBlocks = new ModBlocks();
        modItems = new ModItems();
        modSounds = new ModSounds();

        CurrencyCapabilityManager.register();
        BankCapabilityManager.register();

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
        
        ModAddonManager.init();
    }

    public void initRenderers() {
    }

    public void postInit(FMLPostInitializationEvent event) {
        for (IModModule module : ModModule.MOD_MODULES) {
            module.postInit(event);
        }
        ModAddonManager.postInit();
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
    
    public File getInstanceDirectory() {
        return instanceDirectory;
    }
    
    public File getConfigDirectory() {
        return configDirectory;
    }

    public File getModDirectory() {
        return modDirectory;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public MailSystemManager getMailSystemManager() {
        return mailSystemManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public BankManager getBankManager() {
        return bankManager;
    }

    public void onClentKeyPress(EntityPlayerMP player, ModKey modKey) {
        if (modKey.getName().startsWith("open_wallet_")) {
            for (Currency currency : currencyManager.getCurrencies()) {
                if (currency.getCurrencyWalletInfo().getModKeybind().equals("1") & modKey == ModKey.OPEN_WALLET_1) {
                    openCurrencyWalletGui(player, currency);
                    break;
                }
                if (currency.getCurrencyWalletInfo().getModKeybind().equals("2") & modKey == ModKey.OPEN_WALLET_2) {
                    openCurrencyWalletGui(player, currency);
                    break;
                }
            }
        }
    }

    public void openCurrencyWalletGui(EntityPlayer player, ICurrency currency) {
        if (currency != null) {
            int id = RPGFramework.getProxy().getCurrencyManager().getCurrencyID(currency);
            FMLNetworkHandler.openGui(player, RPGFramework.getInstance(), EnumGuiId.WALLET.ordinal(), player.getEntityWorld(), id, 0, 0);
        }
    }
}
