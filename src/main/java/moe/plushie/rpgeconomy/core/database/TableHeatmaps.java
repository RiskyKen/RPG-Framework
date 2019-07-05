package moe.plushie.rpgeconomy.core.database;

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
        SQLiteDriver.executeUpdate(SQL_CREATE_TABLE);
    }

    private static final String SQL_ADD_HEATMAP = "INSERT INTO heatmaps (id, player_id, x, y, z, dimension, date) VALUES (NULL, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
    public static void addHeatmapData(List<EntityPlayer> players) {
        try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_ADD_HEATMAP)) {
            for (EntityPlayer player : players) {
                DBPlayer dbPlayer = TablePlayers.getPlayer(player.getGameProfile());
                ps.setInt(1, dbPlayer.getId());
                ps.setDouble(2, player.posX);
                ps.setDouble(3, player.posY);
                ps.setDouble(4, player.posZ);
                ps.setInt(5, player.dimension);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
