package moe.plushie.rpgeconomy.core.database;

public final class TableMail {
    
    private TableMail() {
    }
    
    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS heatmaps"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "player_id_sender INTEGER NOT NULL,"
            + "player_id_receiver INTEGER NOT NULL,"
            + "subject VARCHAR(64) NOT NULL,"
            + "text TEXT NOT NULL,"
            + "attachments TEXT NOT NULL,"
            + "sent_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
    public static void create() {
        SQLiteDriver.executeUpdate(SQL_CREATE_TABLE);
    }
}
