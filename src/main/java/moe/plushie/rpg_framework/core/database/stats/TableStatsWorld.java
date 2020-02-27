package moe.plushie.rpg_framework.core.database.stats;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.DatebaseTable;

public final class TableStatsWorld {
    
    private TableStatsWorld() {
    }

    private static DatebaseTable getDatebaseTable() {
        return DatebaseTable.STATS;
    }
    
    private static Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }
    
    public static void create() {
        String sql = "CREATE TABLE IF NOT EXISTS server_stats";
        sql += "player_count INTEGER NOT NULL,";
        sql += "tps TEXT NOT NULL,";
        sql += "entity_count INTEGER NOT NULL,";
        sql += "date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
