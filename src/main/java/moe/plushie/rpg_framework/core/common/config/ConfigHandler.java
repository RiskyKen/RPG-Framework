package moe.plushie.rpg_framework.core.common.config;

import java.io.File;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

    public static final String CATEGORY_GENERAL = "General";
    public static final String CATEGORY_CURRENCY = "Currency";
    public static final String CATEGORY_MAIL = "Mail";
    public static final String CATEGORY_SHOP = "Shop ";
    
    public static Configuration config;
    
    public static ConfigOptions options = new ConfigOptions();
    public static ConfigOptions loaded = new ConfigOptions();
    
    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file, "1");
            loadConfigFile();
        }
    }

    public static void loadConfigFile() {
        loadCategoryGeneral();
        loadCategoryCurrency();
        loadCategoryShop();
        checkIfUpdated();
        if (config.hasChanged()) {
            config.save();
        }
        loaded = options;
    }

    private static void checkIfUpdated() {
        String localVersion = LibModInfo.VERSION;
        if (LibModInfo.VERSION.startsWith("@VER")) {
            return;
        }
        if (versionCompare(options.lastVersion.replaceAll("-", "."), localVersion.replaceAll("-", ".")) < 0) {
            RPGFramework.getLogger().info(String.format("Updated from version %s to version %s.", options.lastVersion, localVersion));
            config.getCategory(CATEGORY_GENERAL).get("lastVersion").set(localVersion);
            if (config.hasChanged()) {
                config.save();
            }
            options.hasUpdated = true;
        } else {
            options.hasUpdated = false;
        }
    }
    
    private static void loadCategoryGeneral() {
        config.setCategoryComment(CATEGORY_GENERAL, "General settings.");
        
        if (!LibModInfo.VERSION.startsWith("@VER")) {
            options.lastVersion = config.getString("lastVersion", CATEGORY_GENERAL, "0.0",
                    "Used by the mod to check if it has been updated.");
        }
        
        options.heatmapTrackingRate = config.getInt("heatmapTrackingRate", CATEGORY_GENERAL, 5, 0, 300,
                "How often in seconds a players location is added to the heatmap. 0 = Disabled.");
    }
    
    private static void loadCategoryCurrency() {
        config.setCategoryComment(CATEGORY_CURRENCY, "Setting to do with the currency system.");
        
        options.showPlayerInventoryInWalletGUI = config.getBoolean("showPlayerInventoryInWalletGUI", CATEGORY_CURRENCY, true, 
                "Is the players inventory shown in the wallet GUI.");
    }
    
    private static void loadCategoryShop() {
        config.setCategoryComment(CATEGORY_SHOP, "Setting to do with the shop system.");
        
        options.showPlayerInventoryInShopGUI = config.getBoolean("showPlayerInventoryInShopGUI", CATEGORY_SHOP, true,
                "Is the players inventory shown in the shop GUI.");
    }

    private static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version
        // string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        else {
            return Integer.signum(vals1.length - vals2.length);
        }
    }
}
