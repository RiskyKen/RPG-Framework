package moe.plushie.rpg_framework.core.database.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.DatebaseTable;

public final class TableStatsServer {

    private TableStatsServer() {
    }

    private static DatebaseTable getDatebaseTable() {
        return DatebaseTable.STATS;
    }

    private static Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

    public static void create() {
        String sql = "CREATE TABLE IF NOT EXISTS server_stats";
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        sql += "date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addColumn("player_count INTEGER DEFAULT 0 NOT NULL");
        addColumn("tick_time FLOAT DEFAULT 0 NOT NULL");
        addColumn("mem_usage INTEGER DEFAULT 0 NOT NULL");
    }

    private static void addColumn(String data) {
        try (Connection conn = getConnection(); Statement s = conn.createStatement()) {
            s.execute("ALTER TABLE server_stats ADD COLUMN " + data);
        } catch (SQLException e) {
            // Column already exists.
        }
    }

    public static void addRecords(int playerCount, float tickTime, int memUsege) {
        String sql = "INSERT INTO server_stats (id, player_count, tick_time, mem_usage, date) VALUES (NULL, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerCount);
            ps.setFloat(2, tickTime);
            ps.setInt(3, memUsege);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
