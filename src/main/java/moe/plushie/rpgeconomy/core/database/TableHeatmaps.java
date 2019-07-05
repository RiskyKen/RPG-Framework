package moe.plushie.rpgeconomy.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.minecraft.entity.player.EntityPlayer;

public final class TableHeatmaps {

    private TableHeatmaps() {
    }

    public static void create() {
        String sql = "CREATE TABLE IF NOT EXISTS heatmaps";
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        sql += "player_id INTEGER NOT NULL,";
        sql += "x DOUBLE NOT NULL,";
        sql += "y DOUBLE NOT NULL,";
        sql += "z DOUBLE NOT NULL,";
        sql += "dimension INTEGER NOT NULL,";
        sql += "date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        SQLiteDriver.executeUpdate(sql);
    }

    public static void addHeatmapData(EntityPlayer player) {
        DBPlayer dbPlayer = TablePlayers.getPlayer(player);
        String sql = "INSERT INTO heatmaps (id, player_id, x, y, z, dimension, date) VALUES (NULL, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dbPlayer.getId());
            ps.setDouble(2, player.posX);
            ps.setDouble(3, player.posY);
            ps.setDouble(4, player.posZ);
            ps.setInt(5, player.dimension);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
