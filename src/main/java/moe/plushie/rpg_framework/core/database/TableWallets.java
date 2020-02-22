package moe.plushie.rpg_framework.core.database;

public final class TableWallets {
    
    private final static String TABLE_NAME = "wallets";
    
    private TableWallets() {
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
        DatabaseManager.executeUpdate(DatebaseTable.PLAYER_DATA, sql);
    }
}
