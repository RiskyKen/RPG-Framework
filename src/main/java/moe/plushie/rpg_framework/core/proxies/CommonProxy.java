package moe.plushie.rpg_framework.core.proxies;

import java.io.File;

import moe.plushie.rpg_framework.api.RpgEconomyAPI;
import moe.plushie.rpg_framework.api.core.IGuiIcon.AnchorHorizontal;
import moe.plushie.rpg_framework.api.core.IGuiIcon.AnchorVertical;
import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.shop.IShop.IShopTab.TabType;
import moe.plushie.rpg_framework.auction.ModuleAuction;
import moe.plushie.rpg_framework.bank.ModuleBank;
import moe.plushie.rpg_framework.bank.common.Bank;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.GuiIcon;
import moe.plushie.rpg_framework.core.common.IdentifierInt;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.ItemMatcherStack;
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
import moe.plushie.rpg_framework.currency.common.Cost.CostFactory;
import moe.plushie.rpg_framework.currency.common.Currency;
import moe.plushie.rpg_framework.currency.common.Currency.CurrencyVariant;
import moe.plushie.rpg_framework.currency.common.Currency.CurrencyWalletInfo;
import moe.plushie.rpg_framework.currency.common.CurrencyManager;
import moe.plushie.rpg_framework.currency.common.Wallet;
import moe.plushie.rpg_framework.currency.common.capability.CurrencyCapabilityManager;
import moe.plushie.rpg_framework.itemData.ItemDataManager;
import moe.plushie.rpg_framework.itemData.ModuleItemData;
import moe.plushie.rpg_framework.mail.ModuleMail;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.MailSystemManager;
import moe.plushie.rpg_framework.shop.ModuleShop;
import moe.plushie.rpg_framework.shop.common.Shop;
import moe.plushie.rpg_framework.shop.common.Shop.ShopItem;
import moe.plushie.rpg_framework.shop.common.Shop.ShopTab;
import moe.plushie.rpg_framework.shop.common.TableShops;
import moe.plushie.rpg_framework.stats.ModuleStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
    private ItemDataManager valueManager;
    private MailSystemManager mailSystemManager;

    private IModModule moduleCurrency;
    private IModModule moduleItemData;
    private IModModule moduleMail;
    private IModModule moduleShop;
    private IModModule moduleBank;
    private IModModule moduleAuction;
    private IModModule moduleStats;

    public void preInit(FMLPreInitializationEvent event) {
        modConfigDirectory = new File(event.getSuggestedConfigurationFile().getParentFile(), "rpg_framework");
        if (!modConfigDirectory.exists()) {
            modConfigDirectory.mkdir();
        }
        ConfigHandler.init(new File(modConfigDirectory, "common.cfg"));
        configDirectory = event.getSuggestedConfigurationFile().getParentFile();
        instanceDirectory = configDirectory.getParentFile();
        modDirectory = new File(instanceDirectory, "rpg_framework");
        if (!modDirectory.exists()) {
            modDirectory.mkdir();
        }

        ModAddonManager.preInit();

        moduleCurrency = new ModuleCurrency();
        moduleItemData = new ModuleItemData(getModDirectory());
        moduleMail = new ModuleMail();
        moduleShop = new ModuleShop(getModDirectory());
        moduleBank = new ModuleBank(getModDirectory());
        moduleAuction = new ModuleAuction();
        moduleStats = new ModuleStats(getModDirectory());

        currencyManager = new CurrencyManager(modDirectory);
        mailSystemManager = new MailSystemManager(modDirectory);

        ReflectionHelper.setPrivateValue(RpgEconomyAPI.class, null, currencyManager, "currencyManager");
        ReflectionHelper.setPrivateValue(RpgEconomyAPI.class, null, mailSystemManager, "mailSystemManager");
        ReflectionHelper.setPrivateValue(RpgEconomyAPI.class, null, ModuleShop.getShopManager(), "shopManager");
        ReflectionHelper.setPrivateValue(RpgEconomyAPI.class, null, ModuleBank.getBankManager(), "bankManager");

        modBlocks = new ModBlocks();
        modItems = new ModItems();
        modSounds = new ModSounds();

        CurrencyCapabilityManager.register();

        for (IModModule module : ModModule.MOD_MODULES) {
            module.preInit(event);
        }

        if (ConfigHandler.optionsLocal.firstRun) {
            RPGFramework.getLogger().info("First run detected, creating example files.");
            createExampleFiles();
        }
    }

    public void createExampleFiles() {
        // Currency
        CurrencyWalletInfo walletInfo = new CurrencyWalletInfo(true, true, "", true, 0F, 0F);
        CurrencyVariant[] currencyVariants = new CurrencyVariant[6];

        currencyVariants[0] = new CurrencyVariant("Copper", 1, new ItemMatcherStack(new ItemStack(ModItems.CURRENCY, 1, 0), true, false));
        currencyVariants[1] = new CurrencyVariant("Silver", 10, new ItemMatcherStack(new ItemStack(ModItems.CURRENCY, 1, 1), true, false));
        currencyVariants[2] = new CurrencyVariant("Gold", 100, new ItemMatcherStack(new ItemStack(ModItems.CURRENCY, 1, 2), true, false));
        currencyVariants[3] = new CurrencyVariant("Platinum", 1000, new ItemMatcherStack(new ItemStack(ModItems.CURRENCY, 1, 3), true, false));
        currencyVariants[4] = new CurrencyVariant("Emerald", 10000, new ItemMatcherStack(new ItemStack(ModItems.CURRENCY, 1, 4), true, false));
        currencyVariants[5] = new CurrencyVariant("Diamond", 100000, new ItemMatcherStack(new ItemStack(ModItems.CURRENCY, 1, 5), true, false));
        Currency currency = new Currency(new IdentifierString("money.json"), "Money", "%d", walletInfo, currencyVariants);
        currencyManager.saveCurrency(currency);

        // Shop
        Shop shop = new Shop(new IdentifierInt(-1), "Example Shop");

        ShopTab shopTabLeatherArmour = new ShopTab("Example Tab Leather Armour", 1, TabType.BUY, 1F);
        shopTabLeatherArmour.getItems().set(0, new ShopItem(new ItemStack(Items.LEATHER_HELMET), CostFactory.newCost().addWalletCosts(new Wallet(currency, 1)).build()));
        shopTabLeatherArmour.getItems().set(1, new ShopItem(new ItemStack(Items.LEATHER_CHESTPLATE), CostFactory.newCost().addWalletCosts(new Wallet(currency, 3)).build()));
        shopTabLeatherArmour.getItems().set(2, new ShopItem(new ItemStack(Items.LEATHER_LEGGINGS), CostFactory.newCost().addWalletCosts(new Wallet(currency, 2)).build()));
        shopTabLeatherArmour.getItems().set(3, new ShopItem(new ItemStack(Items.LEATHER_BOOTS), CostFactory.newCost().addWalletCosts(new Wallet(currency, 1)).build()));
        shop.getTabs().add(shopTabLeatherArmour);

        ShopTab shopTabIronArmour = new ShopTab("Example Tab Iron Armour", 2, TabType.BUY, 1F);
        shopTabIronArmour.getItems().set(0, new ShopItem(new ItemStack(Items.IRON_HELMET), CostFactory.newCost().addWalletCosts(new Wallet(currency, 2)).build()));
        shopTabIronArmour.getItems().set(1, new ShopItem(new ItemStack(Items.IRON_CHESTPLATE), CostFactory.newCost().addWalletCosts(new Wallet(currency, 6)).build()));
        shopTabIronArmour.getItems().set(2, new ShopItem(new ItemStack(Items.IRON_LEGGINGS), CostFactory.newCost().addWalletCosts(new Wallet(currency, 5)).build()));
        shopTabIronArmour.getItems().set(3, new ShopItem(new ItemStack(Items.IRON_BOOTS), CostFactory.newCost().addWalletCosts(new Wallet(currency, 2)).build()));
        shop.getTabs().add(shopTabIronArmour);

        ShopTab shopTabMelee = new ShopTab("Example Tab Melee", 5, TabType.BUY, 1F);
        shopTabMelee.getItems().set(0, new ShopItem(new ItemStack(Items.WOODEN_SWORD), CostFactory.newCost().addWalletCosts(new Wallet(currency, 4)).build()));
        shopTabMelee.getItems().set(1, new ShopItem(new ItemStack(Items.STONE_SWORD), CostFactory.newCost().addWalletCosts(new Wallet(currency, 5)).build()));
        shopTabMelee.getItems().set(2, new ShopItem(new ItemStack(Items.IRON_SWORD), CostFactory.newCost().addWalletCosts(new Wallet(currency, 6)).build()));
        shopTabMelee.getItems().set(3, new ShopItem(new ItemStack(Items.GOLDEN_SWORD), CostFactory.newCost().addWalletCosts(new Wallet(currency, 4)).build()));
        shopTabMelee.getItems().set(4, new ShopItem(new ItemStack(Items.DIAMOND_SWORD), CostFactory.newCost().addWalletCosts(new Wallet(currency, 7)).build()));
        shopTabMelee.getItems().set(5, new ShopItem(new ItemStack(Items.SHIELD), CostFactory.newCost().addWalletCosts(new Wallet(currency, 5)).build()));
        shop.getTabs().add(shopTabMelee);

        ShopTab shopTabRanged = new ShopTab("Example Tab Ranged", 12, TabType.BUY, 1F);
        shopTabRanged.getItems().set(0, new ShopItem(new ItemStack(Items.BOW), CostFactory.newCost().addWalletCosts(new Wallet(currency, 4)).build()));
        shopTabRanged.getItems().set(1, new ShopItem(new ItemStack(Items.ARROW), CostFactory.newCost().addWalletCosts(new Wallet(currency, 1)).build()));
        shopTabRanged.getItems().set(2, new ShopItem(new ItemStack(Items.ARROW, 16), CostFactory.newCost().addWalletCosts(new Wallet(currency, 16)).build()));
        shopTabRanged.getItems().set(3, new ShopItem(new ItemStack(Items.ARROW, 32), CostFactory.newCost().addWalletCosts(new Wallet(currency, 32)).build()));
        shop.getTabs().add(shopTabRanged);

        ShopTab shopTabItem = new ShopTab("Example Tab Item Cost", 15, TabType.BUY, 1F);
        IItemMatcher itemMatcherCobble = new ItemMatcherStack(new ItemStack(Blocks.COBBLESTONE), true, false);
        shopTabItem.getItems().set(0, new ShopItem(new ItemStack(Blocks.STONE), CostFactory.newCost().addItemCosts(itemMatcherCobble).build()));
        shop.getTabs().add(shopTabItem);

        TableShops.addNewShop(shop);

        // Mail System
        MailSystem mailSystem = new MailSystem(new IdentifierString("mail.json"), "Mail System");
        GuiIcon guiIconMain = new GuiIcon(new String[] { "net.minecraft.client.gui.GuiChat", "" }, AnchorHorizontal.RIGHT, AnchorVertical.TOP, -5, 5, 19, 0.75F);
        GuiIcon guiIconInventory = new GuiIcon(new String[] { "net.minecraft.client.gui.inventory.GuiInventory" }, AnchorHorizontal.CENTER, AnchorVertical.CENTER, 76, -70, 19, 0.75F);
        mailSystem.setCurrency(currency);
        mailSystem.setMessageCost(1);
        mailSystem.setAttachmentCost(5);
        mailSystem.setGuiIcons(new GuiIcon[] { guiIconMain, guiIconInventory });
        mailSystemManager.saveMailSystem(mailSystem);

        // Bank
        Bank bank = new Bank(new IdentifierString("bank.json"));
        bank.setName("Bank");
        ICost[] unlockCosts = new ICost[bank.getTabUnlockableCount()];
        for (int i = 0; i < unlockCosts.length; i++) {
            unlockCosts[i] = CostFactory.newCost().addWalletCosts(new Wallet(currency, (i + 1) * 100)).build();
        }
        bank.setTabUnlockCosts(unlockCosts);
        ModuleBank.getBankManager().saveBank(bank);

        // Send changes to clients.
        currencyManager.reload(true);
        mailSystemManager.reload(true);
        ModuleBank.getBankManager().reload(true);
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
    
    public File getModConfigDirectory() {
        return modConfigDirectory;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public MailSystemManager getMailSystemManager() {
        return mailSystemManager;
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
