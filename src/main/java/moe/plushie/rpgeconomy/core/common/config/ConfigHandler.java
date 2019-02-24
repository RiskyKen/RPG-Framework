package moe.plushie.rpgeconomy.core.common.config;

import java.io.File;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

    public static final String CATEGORY_GENERAL = "General";
    public static final String CATEGORY_CURRENCY = "Currency";
    
    public static Configuration config;
    
    // General
    
    
    // Currency
    public static boolean walletNeedItemToOpen = false;
    public static boolean walletOpenWithKeybind = true;
    
    // Other
    public static String lastVersion;
    public static boolean hasUpdated;
    
    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file, "1");
            loadConfigFile();
        }
    }

    public static void loadConfigFile() {
        loadCategoryGeneral();
        loadCategoryCurrency();
        checkIfUpdated();
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    private static void checkIfUpdated() {
        String localVersion = LibModInfo.VERSION;
        if (LibModInfo.VERSION.startsWith("@VER")) {
            return;
        }
        if (versionCompare(lastVersion.replaceAll("-", "."), localVersion.replaceAll("-", ".")) < 0) {
            RpgEconomy.getLogger().info(String.format("Updated from version %s to version %s.", lastVersion, localVersion));
            config.getCategory(CATEGORY_GENERAL).get("lastVersion").set(localVersion);
            if (config.hasChanged()) {
                config.save();
            }
            hasUpdated = true;
        } else {
            hasUpdated = false;
        }
    }
    
    private static void loadCategoryGeneral() {
        config.setCategoryComment(CATEGORY_GENERAL, "General settings.");
        
        
        
        if (!LibModInfo.VERSION.startsWith("@VER")) {
            lastVersion = config.getString("lastVersion", CATEGORY_GENERAL, "0.0",
                    "Used by the mod to check if it has been updated.");
        }
    }
    
    private static void loadCategoryCurrency() {
        config.setCategoryComment(CATEGORY_CURRENCY, "Setting to do with the currency system.");
        
        walletNeedItemToOpen = config.getBoolean("walletNeedItemToOpen", CATEGORY_CURRENCY, false, 
                "Does the wallet item need to be in the players inventory to open the wallet GUI.");
        
        walletOpenWithKeybind = config.getBoolean("walletOpenWithKeybind", CATEGORY_CURRENCY, true,
                "Can the wallet be opened by pressing a keybind.");
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
