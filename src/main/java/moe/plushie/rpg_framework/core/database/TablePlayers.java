package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

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

    public static boolean isPlayerInDatabase(GameProfile gameProfile) {
        return getPlayer(gameProfile) != DBPlayer.MISSING;
    }

    public static void addPlayerToDatabase(GameProfile gameProfile) {
        String sql = "INSERT INTO players (id, uuid, username, first_seen, last_seen) VALUES (NULL, '%s', '%s', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        sql = String.format(sql, gameProfile.getId().toString(), gameProfile.getName());
        SQLiteDriver.executeUpdate(sql);
    }

    public static void updatePlayerLastLogin(GameProfile gameProfile) {
        String sql = "UPDATE players SET username='%s', last_seen=datetime('now') WHERE uuid='%s'";
        sql = String.format(sql, gameProfile.getName(), gameProfile.getId().toString());
        SQLiteDriver.executeUpdate(sql);
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
    
    public static DBPlayer getPlayer(GameProfile gameProfile) {
        DBPlayer playerInfo = DBPlayer.MISSING;
        try (Connection conn = SQLiteDriver.getConnection()) {
            if (gameProfile.getId() != null) {
                playerInfo = getPlayer(conn, gameProfile.getId());
            } else {
                playerInfo = getPlayer(conn, gameProfile.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerInfo;
    }
    
    private static final String SQL_GET_PLAYER_USERNAME = "SELECT id FROM players WHERE username=?";
    public static DBPlayer getPlayer(Connection conn, String username) {
        DBPlayer playerInfo = DBPlayer.MISSING;
        try (PreparedStatement ps = conn.prepareStatement(SQL_GET_PLAYER_USERNAME)) {
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                playerInfo = new DBPlayer(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerInfo;
    }
    
    private static final String SQL_GET_PLAYER_UUID = "SELECT id FROM players WHERE uuid=?";
    public static DBPlayer getPlayer(Connection conn, UUID uuid) {
        DBPlayer playerInfo = DBPlayer.MISSING;
        try (PreparedStatement ps = conn.prepareStatement(SQL_GET_PLAYER_UUID)) {
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                playerInfo = new DBPlayer(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerInfo;
    }
}
