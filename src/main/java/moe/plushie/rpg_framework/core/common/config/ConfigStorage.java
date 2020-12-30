package moe.plushie.rpg_framework.core.common.config;

import java.io.File;

import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import net.minecraftforge.common.config.Configuration;

public class ConfigStorage {

    public static final String CATEGORY_GENERAL = "General";
    public static final String CATEGORY_JSON = "JSON";
    public static final String CATEGORY_SQLITE = "SQLite";
    public static final String CATEGORY_MY_SQL = "MySQL";

    private static Configuration config;

    private static StorageType storageType = StorageType.SQLITE;
    private static String mySqlHost = "localhost";
    private static int mySqlPort = 3306;
    private static String mySqlUsername = "";
    private static String mySqlPassword = "";
    private static String mySqlDatabase = LibModInfo.ID;
    private static int mySqlTimeout = 60 * 1000;

    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file, "1");
            loadConfigFile();
        }
    }

    public static void loadConfigFile() {
        loadCategoryGeneral();
        loadCategoryJson();
        loadCategorySqlite();
        loadCategoryMySql();
        if (config.hasChanged()) {
            config.save();
        }
    }

    private static void loadCategoryGeneral() {
        config.setCategoryComment(CATEGORY_GENERAL, "General settings.");

        String storageTypeStr = config.getString("storage_type", CATEGORY_GENERAL, "sqlite", "Select the storage type use by the mod.\n" + "Valid values:\n" + "json (!!! for debugging do not use !!!)\n" + "sqlite\n" + "mysql\n");
        try {
            storageType = StorageType.valueOf(storageTypeStr.toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadCategoryJson() {
        config.setCategoryComment(CATEGORY_JSON, "JSON storage settings. Only active if storage_type is set to json.");
    }

    private static void loadCategorySqlite() {
        config.setCategoryComment(CATEGORY_SQLITE, "SQLite storage settings. Only active if storage_type is set to sqlite.");

        config.getString("table_file_players", CATEGORY_SQLITE, "player_data", "File that the players table will be stored in.");
        config.getString("table_file_mail", CATEGORY_SQLITE, "player_data", "File that the mail table will be stored in.");
        config.getString("table_file_bank_accounts", CATEGORY_SQLITE, "player_data", "File that the bank accounts table will be stored in.");
        config.getString("table_file_wallets", CATEGORY_SQLITE, "player_data", "File that the wallets table will be stored in.");

        config.getString("table_file_shops", CATEGORY_SQLITE, "data", "File that the players table will be stored in.");
        config.getString("table_file_item_data", CATEGORY_SQLITE, "data", "File that the item data table will be stored in.");
        config.getString("table_file_loot_tables", CATEGORY_SQLITE, "data", "File that the loot tables table will be stored in.");
        config.getString("table_file_loot_pools", CATEGORY_SQLITE, "data", "File that the loot pools table will be stored in.");

        config.getString("table_file_heatmaps", CATEGORY_SQLITE, "stats", "File that the heatmaps table will be stored in.");
        config.getString("table_file_server_stats", CATEGORY_SQLITE, "stats", "File that the server stats table will be stored in.");
        config.getString("table_file_world_stats", CATEGORY_SQLITE, "stats", "File that the world stats table will be stored in.");
        config.getString("table_file_shop_sales", CATEGORY_SQLITE, "stats", "File that the shop sales table will be stored in.");
    }

    private static void loadCategoryMySql() {
        config.setCategoryComment(CATEGORY_MY_SQL, "MySQL storage settings. Only active if storage_type is set to mysql.");

        mySqlHost = config.getString("host", CATEGORY_MY_SQL, "localhost", "Host address of the server");
        mySqlPort = config.getInt("port", CATEGORY_MY_SQL, 3306, 1, Integer.MAX_VALUE, "Host port of the server");
        mySqlUsername = config.getString("username", CATEGORY_MY_SQL, "", "Username used to access the server.");
        mySqlPassword = config.getString("password", CATEGORY_MY_SQL, "", "Password use to access the server.");
        mySqlDatabase = config.getString("database", CATEGORY_MY_SQL, LibModInfo.ID, "Database to use.");
        mySqlTimeout = config.getInt("timeout", CATEGORY_MY_SQL, 10 * 1000, 0, 60 * 1000, "Connection time in milliseconds.");
    }

    public static StorageType getStorageType() {
        return storageType;
    }

    public static String getMySqlHost() {
        return mySqlHost;
    }

    public static int getMySqlPort() {
        return mySqlPort;
    }

    public static String getMySqlUsername() {
        return mySqlUsername;
    }

    public static String getMySqlPassword() {
        return mySqlPassword;
    }

    public static String getMySqlDatabase() {
        return mySqlDatabase;
    }

    public static int getMySqlTimeout() {
        return mySqlTimeout;
    }

    public enum StorageType {
        JSON, SQLITE, MYSQL
    }
}
