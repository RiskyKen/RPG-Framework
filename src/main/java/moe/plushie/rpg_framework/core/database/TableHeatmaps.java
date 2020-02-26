package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public final class TableHeatmaps {

    private TableHeatmaps() {
    }

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS heatmaps"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "player_id INTEGER NOT NULL,"
            + "x DOUBLE NOT NULL,"
            + "y DOUBLE NOT NULL,"
            + "z DOUBLE NOT NULL,"
            + "dimension INTEGER NOT NULL,"
            + "date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
    public static void create() {
        DatabaseManager.executeUpdate(DatebaseTable.STATS, SQL_CREATE_TABLE);
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
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
