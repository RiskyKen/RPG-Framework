package moe.plushie.rpgeconomy.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import net.minecraft.entity.player.EntityPlayer;

public final class Database {

    public static final PlayersTable PLAYERS_TABLE = new PlayersTable();
    public static final HeatmapsTable HEATMAPS_TABLE = new HeatmapsTable();
    public static final BanksTable BANKS_TABLE = new BanksTable();

    public static final class PlayersTable {

        private PlayersTable() {
        }

        public void create() {
            String sql = "CREATE TABLE IF NOT EXISTS players ";
            sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
            sql += "uuid VARCHAR(36) NOT NULL,";
            sql += "username VARCHAR(80) NOT NULL,";
            sql += "first_seen DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,";
            sql += "last_seen DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
            SQLiteDriver.executeUpdate(sql);
        }

        public boolean isPlayerInDatabase(EntityPlayer player) {
            String sql = "SELECT * FROM players WHERE uuid='%s'";
            sql = String.format(sql, player.getGameProfile().getId().toString());
            ArrayList<String> results = SQLiteDriver.executeQueryArrayList(sql);
            return !results.isEmpty();
        }

        public void addPlayerToDatabase(EntityPlayer player) {
            String sql = "INSERT INTO players (id, uuid, username, first_seen, last_seen) VALUES (NULL, '%s', '%s', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
            sql = String.format(sql, player.getGameProfile().getId().toString(), player.getGameProfile().getName());
            SQLiteDriver.executeUpdate(sql);
        }

        public void updatePlayerLastLogin(EntityPlayer player) {
            String sql = "UPDATE players SET last_seen=datetime('now') WHERE uuid='%s'";
            Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
            sql = String.format(sql, player.getGameProfile().getId().toString());
            SQLiteDriver.executeUpdate(sql);
        }
        
        public int getPlayerId(String username) {
            String sql = "SELECT id FROM players WHERE username='%s'";
            sql = String.format(sql, username);
            ArrayList<String> results = SQLiteDriver.executeQueryArrayList(sql);
            if (!results.isEmpty()) {
                return Integer.parseInt(results.get(0));
            }
            return -1;
        }

        public int getPlayerId(EntityPlayer player) {
            String sql = "SELECT id FROM players WHERE uuid='%s'";
            sql = String.format(sql, player.getGameProfile().getId().toString());
            ArrayList<String> results = SQLiteDriver.executeQueryArrayList(sql);
            if (!results.isEmpty()) {
                return Integer.parseInt(results.get(0));
            }
            return -1;
        }
    }

    public static final class HeatmapsTable {

        private HeatmapsTable() {
        }

        public void create() {
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

        public void addHeatmapData(EntityPlayer player) {
            int playerId = PLAYERS_TABLE.getPlayerId(player);
            if (playerId == -1) {
                return;
            }
            String sql = "INSERT INTO heatmaps (id, player_id, x, y, z, dimension, date) VALUES (NULL, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, playerId);
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

    public static final class BanksTable {

        private BanksTable() {
        }

        public void create() {
            String sql = "CREATE TABLE IF NOT EXISTS banks";
            sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
            sql += "player_id INTEGER NOT NULL,";
            sql += "bank_identifier TEXT NOT NULL,";
            sql += "tabs TEXT NOT NULL,";
            sql += "times_opened INTEGER NOT NULL,";
            sql += "last_access DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,";
            sql += "last_change DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
            SQLiteDriver.executeUpdate(sql);
        }

        public String getAccountTabs(EntityPlayer player, String bankIdentifier) {
            int playerId = PLAYERS_TABLE.getPlayerId(player);
            String tabs = null;
            try (Connection conn = SQLiteDriver.getConnection(); ) {
                String sqlUpdate = "UPDATE banks SET times_opened = times_opened + 1, last_access=datetime('now') WHERE bank_identifier=? AND player_id=?";
                try(PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setString(1, bankIdentifier);
                    ps.setInt(2, playerId);
                    ps.executeUpdate();
                }
                
                String sqlGetTabs = "SELECT * FROM banks WHERE bank_identifier=? AND player_id=?";
                try(PreparedStatement ps = conn.prepareStatement(sqlGetTabs)) {
                    ps.setString(1, bankIdentifier);
                    ps.setInt(2, playerId);
                    ResultSet resultSet = ps.executeQuery();
                    while (resultSet.next()) {
                        tabs = resultSet.getString("tabs");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return tabs;
        }

        public int setAccount(EntityPlayer player, String bankIdentifier, String tabs) {
            int playerId = PLAYERS_TABLE.getPlayerId(player);
            int id = -1;
            String sql = "INSERT INTO banks (id, player_id, bank_identifier, tabs, times_opened, last_access, last_change) VALUES (NULL, ?, ?, ?, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
            try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, playerId);
                ps.setString(2, bankIdentifier);
                ps.setString(3, tabs);
                id = ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return id;
        }

        public void updateAccount(EntityPlayer player, String bankIdentifier, String tabs) {
            int playerId = PLAYERS_TABLE.getPlayerId(player);
            String sql = "UPDATE banks SET tabs=?, last_access=datetime('now'), last_change=datetime('now') WHERE player_id=? AND bank_identifier=?";
            try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tabs);
                ps.setInt(2, playerId);
                ps.setString(3, bankIdentifier);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public boolean isAccountInDatabase(EntityPlayer player, String bankIdentifier) {
            boolean foundAccount = false;
            int playerId = PLAYERS_TABLE.getPlayerId(player);
            String sql = "SELECT * FROM banks WHERE bank_identifier=? AND player_id=?";
            try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, bankIdentifier);
                ps.setInt(2, playerId);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    foundAccount = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return foundAccount;
        }
    }
}
