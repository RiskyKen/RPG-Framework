package moe.plushie.rpg_framework.core.database;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.itemData.ItemData;

public final class TableItemData {

    private final static String TABLE_NAME = "item_data";
    
    private TableItemData() {
    }

    public static void create() {
        String sql = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME;
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        sql += "player_id INTEGER NOT NULL,";
        sql += "currency_identifier TEXT NOT NULL,";
        sql += "amount INTEGER NOT NULL,";
        sql += "times_opened INTEGER NOT NULL,";
        sql += "last_access DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,";
        sql += "last_change DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        DatabaseManager.executeUpdate(DatebaseTable.STATS, sql);
    }

    public static IItemData getItemData(IItemMatcher itemMatcher) {
        // TODO Auto-generated method stub
        return ItemData.ITEM_DATA_MISSING;
    }

    public static void setItemData(IItemMatcher itemMatcher, IItemData itemData) {
        // TODO Auto-generated method stub
    }
}
