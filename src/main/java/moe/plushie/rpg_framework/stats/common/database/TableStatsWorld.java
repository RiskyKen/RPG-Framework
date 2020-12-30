package moe.plushie.rpg_framework.stats.common.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder.ISqlBulderCreateTable;

public final class TableStatsWorld {

    private final static String TABLE_NAME = "world_stats";
    
    private TableStatsWorld() {
    }

    private static DatebaseTable getDatebaseTable() {
        return DatebaseTable.STATS;
    }

    private static Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

    public static void create() {
        ISqlBulderCreateTable table = DatabaseManager.getSqlBulder().createTable(TABLE_NAME);
        table.addColumn("id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true).setAutoIncrement(true);
        table.addColumn("dimension", ISqlBulder.DataType.INT).setNotNull(true);
        table.addColumn("player_count", ISqlBulder.DataType.INT).setUnsigned(true).setUnsigned(true).setNotNull(true);
        table.addColumn("tick_time", ISqlBulder.DataType.FLOAT).setUnsigned(true).setNotNull(true);
        table.addColumn("entity_count", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true);
        table.addColumn("tile_count", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true);
        table.addColumn("ticking_tile_count", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true);
        table.addColumn("date", ISqlBulder.DataType.DATETIME).setNotNull(true).setDefault("CURRENT_TIMESTAMP");
        table.ifNotExists(true);
        table.setPrimaryKey("id");
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(table.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
//        String sql = "CREATE TABLE IF NOT EXISTS world_stats";
//        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
//        sql += "date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
//        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
//            statement.executeUpdate(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        addColumn("dimension INTEGER DEFAULT 0 NOT NULL");
//        addColumn("player_count INTEGER DEFAULT 0 NOT NULL");
//        addColumn("tick_time FLOAT DEFAULT 0 NOT NULL");
//        addColumn("entity_count INTEGER DEFAULT 0 NOT NULL");
//        addColumn("tile_count INTEGER DEFAULT 0 NOT NULL");
//        addColumn("ticking_tile_count INTEGER DEFAULT 0 NOT NULL");
    }

    private static void addColumn(String data) {
        try (Connection conn = getConnection(); Statement s = conn.createStatement()) {
            s.execute("ALTER TABLE world_stats ADD COLUMN " + data);
        } catch (SQLException e) {
            // Column already exists.
        }
    }

    public static void addRecords(int dimension, int playerCount, float tickTime, int entityCount, int tileCount, int tickingTileCount) {
        String sql = "INSERT INTO world_stats (id, dimension, player_count, tick_time, entity_count, tile_count, ticking_tile_count, date)";
        sql += " VALUES (NULL, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dimension);
            ps.setInt(2, playerCount);
            ps.setFloat(3, tickTime);
            ps.setInt(4, entityCount);
            ps.setInt(5, tileCount);
            ps.setInt(6, tickingTileCount);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
