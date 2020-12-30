package moe.plushie.rpg_framework.stats.common.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import moe.plushie.rpg_framework.core.common.database.DBPlayer;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.TablePlayers;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder.ISqlBulderCreateTable;
import net.minecraft.entity.player.EntityPlayer;

public final class TableHeatmaps {

    private final static String TABLE_NAME = "heatmaps";

    private TableHeatmaps() {
    }

    private static DatebaseTable getDatebaseTable() {
        return DatebaseTable.STATS;
    }

    private static Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

//    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS heatmaps"
//            + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
//            + "player_id INTEGER NOT NULL,"
//            + "x DOUBLE NOT NULL,"
//            + "y DOUBLE NOT NULL,"
//            + "z DOUBLE NOT NULL,"
//            + "dimension INTEGER NOT NULL,"
//            + "date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";

    public static void create() {
        ISqlBulderCreateTable table = DatabaseManager.getSqlBulder().createTable(TABLE_NAME);
        table.addColumn("id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true).setAutoIncrement(true);
        table.addColumn("player_id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true);
        table.addColumn("x", ISqlBulder.DataType.DOUBLE).setNotNull(true);
        table.addColumn("y", ISqlBulder.DataType.DOUBLE).setNotNull(true);
        table.addColumn("z", ISqlBulder.DataType.DOUBLE).setNotNull(true);
        table.addColumn("dimension", ISqlBulder.DataType.INT).setNotNull(true);
        table.addColumn("date", ISqlBulder.DataType.DATETIME).setDefault("CURRENT_TIMESTAMP").setNotNull(true);
        table.ifNotExists(true);
        table.setPrimaryKey("id");
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(table.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
//            statement.executeUpdate(SQL_CREATE_TABLE);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    private static final String SQL_ADD_HEATMAP = "INSERT INTO heatmaps (id, player_id, x, y, z, dimension, date) VALUES (NULL, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

    public static PreparedStatement createPreStateHeatmapAdd(Connection conn) throws SQLException {
        return conn.prepareStatement(SQL_ADD_HEATMAP);
    }

    public static void addHeatmapData(List<EntityPlayer> players) {
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.STATS); PreparedStatement psHeatmap = createPreStateHeatmapAdd(conn)) {
            conn.setAutoCommit(false);
            for (EntityPlayer player : players) {
                DBPlayer dbPlayer = TablePlayers.getPlayerInfo(player.getGameProfile());
                psHeatmap.setInt(1, dbPlayer.getId());
                psHeatmap.setDouble(2, player.posX);
                psHeatmap.setDouble(3, player.posY);
                psHeatmap.setDouble(4, player.posZ);
                psHeatmap.setInt(5, player.dimension);
                psHeatmap.addBatch();
            }
            psHeatmap.executeBatch();
            conn.commit();
            psHeatmap.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
