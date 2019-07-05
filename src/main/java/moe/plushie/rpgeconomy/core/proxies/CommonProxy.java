package moe.plushie.rpgeconomy.core.proxies;

import java.io.File;

import moe.plushie.rpgeconomy.api.RpgEconomyAPI;
import moe.plushie.rpgeconomy.api.currency.ICurrency;
import moe.plushie.rpgeconomy.auction.ModuleAuction;
import moe.plushie.rpgeconomy.bank.ModuleBank;
import moe.plushie.rpgeconomy.bank.common.BankManager;
import moe.plushie.rpgeconomy.bank.common.capability.BankCapabilityManager;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.command.CommandRpg;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.init.ModBlocks;
import moe.plushie.rpgeconomy.core.common.init.ModItems;
import moe.plushie.rpgeconomy.core.common.init.ModSounds;
import moe.plushie.rpgeconomy.core.common.init.ModTiles;
import moe.plushie.rpgeconomy.core.common.lib.LibGuiIds;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.common.lib.LibModKeys.ModKey;
import moe.plushie.rpgeconomy.core.common.module.IModModule;
import moe.plushie.rpgeconomy.core.common.module.ModModule;
import moe.plushie.rpgeconomy.core.common.network.GuiHandler;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.currency.ModuleCurrency;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.CurrencyManager;
import moe.plushie.rpgeconomy.currency.common.capability.CurrencyCapabilityManager;
import moe.plushie.rpgeconomy.mail.ModuleMail;
import moe.plushie.rpgeconomy.mail.common.MailSystemManager;
import moe.plushie.rpgeconomy.shop.ModuleShop;
import moe.plushie.rpgeconomy.shop.common.ShopManager;
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
    private File instanceDirectory;
    private File modDirectory;

    private ModBlocks modBlocks;
    private ModItems modItems;
    private ModSounds modSounds;

    private CurrencyManager currencyManager;
    private MailSystemManager mailSystemManager;
    private ShopManager shopManager;
    private BankManager bankManager;

    private IModModule moduleCurrency = new ModuleCurrency();
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
        instanceDirectory = event.getSuggestedConfigurationFile().getParentFile().getParentFile();
        modDirectory = new File(instanceDirectory, LibModInfo.ID);
        if (!modDirectory.exists()) {
            modDirectory.mkdir();
        }

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
            int id = RpgEconomy.getProxy().getCurrencyManager().getCurrencyID(currency);
            FMLNetworkHandler.openGui(player, RpgEconomy.getInstance(), LibGuiIds.WALLET, player.getEntityWorld(), id, 0, 0);
        }
    }
}
