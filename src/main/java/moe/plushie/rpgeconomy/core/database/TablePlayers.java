package moe.plushie.rpgeconomy.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;

public final class TablePlayers {

    private TablePlayers() {
    }

    public static void create() {
        String sql = "CREATE TABLE IF NOT EXISTS players ";
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        sql += "uuid VARCHAR(36) NOT NULL,";
        sql += "username VARCHAR(80) NOT NULL,";
        sql += "first_seen DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,";
        sql += "last_seen DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        SQLiteDriver.executeUpdate(sql);
    }

    public static boolean isPlayerInDatabase(EntityPlayer player) {
        return getPlayer(player.getGameProfile()) != DBPlayer.MISSING;
    }

    public static void addPlayerToDatabase(EntityPlayer player) {
        String sql = "INSERT INTO players (id, uuid, username, first_seen, last_seen) VALUES (NULL, '%s', '%s', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        sql = String.format(sql, player.getGameProfile().getId().toString(), player.getGameProfile().getName());
        SQLiteDriver.executeUpdate(sql);
    }

    public static void updatePlayerLastLogin(EntityPlayer player) {
        String sql = "UPDATE players SET username='%s', last_seen=datetime('now') WHERE uuid='%s'";
        Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
        sql = String.format(sql, player.getGameProfile().getName(), player.getGameProfile().getId().toString());
        SQLiteDriver.executeUpdate(sql);
    }

    public static DBPlayerInfo getPlayerInfo(EntityPlayer player) {
        return getPlayerInfo(player.getGameProfile());
    }

    public static DBPlayerInfo getPlayerInfo(GameProfile gameProfile) {
        String sql = "SELECT * FROM players WHERE ";
        String searchValue;
        if (gameProfile.getId() != null) {
            sql += "uuid=?";
            searchValue = gameProfile.getId().toString();
        } else {
            sql += "username=?";
            searchValue = gameProfile.getName();
        }
        DBPlayerInfo playerInfo = null;
        try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, searchValue);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String username = resultSet.getString("username");
                Date firstSeen = resultSet.getDate("first_seen");
                Date lastLogin = resultSet.getDate("last_seen");
                playerInfo = new DBPlayerInfo(id, new GameProfile(uuid, username), firstSeen, lastLogin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerInfo;
    }

    public static DBPlayer getPlayer(EntityPlayer player) {
        return getPlayer(player.getGameProfile());
    }

    public static DBPlayer getPlayer(GameProfile gameProfile) {
        String sql = "SELECT id FROM players WHERE ";
        String searchValue;
        if (gameProfile.getId() != null) {
            sql += "uuid=?";
            searchValue = gameProfile.getId().toString();
        } else {
            sql += "username=?";
            searchValue = gameProfile.getName();
        }
        DBPlayer playerInfo = DBPlayer.MISSING;
        try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, searchValue);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                playerInfo = new DBPlayer(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerInfo;
    }
}
