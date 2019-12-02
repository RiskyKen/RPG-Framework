package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.core.database.driver.SQLiteDriver;

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
        DatabaseManager.executeUpdate(sql);
    }

    public static boolean isPlayerInDatabase(GameProfile gameProfile) {
        return getPlayer(gameProfile) != DBPlayer.MISSING;
    }

    public static void addPlayerToDatabase(GameProfile gameProfile) {
        String sql = "INSERT INTO players (id, uuid, username, first_seen, last_seen) VALUES (NULL, '%s', '%s', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        sql = String.format(sql, gameProfile.getId().toString(), gameProfile.getName());
        DatabaseManager.executeUpdate(sql);
    }

    public static void updatePlayerLastLogin(GameProfile gameProfile) {
        String sql = "UPDATE players SET username='%s', last_seen=datetime('now') WHERE uuid='%s'";
        sql = String.format(sql, gameProfile.getName(), gameProfile.getId().toString());
        DatabaseManager.executeUpdate(sql);
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
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, searchValue);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String username = resultSet.getString("username");
                Timestamp firstSeen = resultSet.getTimestamp("first_seen");
                Timestamp lastLogin = resultSet.getTimestamp("last_seen");
                playerInfo = new DBPlayerInfo(id, new GameProfile(uuid, username), firstSeen, lastLogin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerInfo;
    }

    public static DBPlayer getPlayer(GameProfile gameProfile) {
        DBPlayer playerInfo = DBPlayer.MISSING;
        try (Connection conn = DatabaseManager.getConnection()) {
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
    
    private static final String SQL_GET_PLAYER_USERNAME = "SELECT id FROM players WHERE username=? COLLATE NOCASE";
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
    
    public static PreparedStatement createPreStatementPlayerUUID(Connection conn) throws SQLException {
        return conn.prepareStatement(SQL_GET_PLAYER_UUID);
    }
    
    public static DBPlayer getPlayer(Connection conn, UUID uuid) {
        DBPlayer playerInfo = DBPlayer.MISSING;
        try (PreparedStatement ps = createPreStatementPlayerUUID(conn)) {
            playerInfo = getPlayerUUID(conn, ps, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerInfo;
    }
    
    public static DBPlayer getPlayerUUID(Connection conn, PreparedStatement ps, UUID uuid) throws SQLException {
        DBPlayer playerInfo = DBPlayer.MISSING;
        ps.setString(1, uuid.toString());
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            playerInfo = new DBPlayer(resultSet.getInt("id"));
        }
        return playerInfo;
    }
    
    private static final String SQL_GET_PLAYER_ID = "SELECT * FROM players WHERE id=?";
    
    public static DBPlayerInfo getPlayer(int id) {
        DBPlayerInfo playerInfo = DBPlayerInfo.MISSING_INFO;
        try (Connection conn = DatabaseManager.getConnection()) {
            return getPlayer(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerInfo;
    }
    
    public static DBPlayerInfo getPlayer(Connection conn, int id) {
        DBPlayerInfo playerInfo = DBPlayerInfo.MISSING_INFO;
        try (PreparedStatement ps = conn.prepareStatement(SQL_GET_PLAYER_ID)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
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
}
