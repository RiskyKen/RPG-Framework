package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.minecraft.entity.player.EntityPlayer;

public final class TableBankAccounts {

    private final static String TABLE_NAME = "bank_accounts";
    
    private TableBankAccounts() {
    }

    public static void create() {
        String sql = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME;
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        sql += "player_id INTEGER NOT NULL,";
        sql += "bank_identifier TEXT NOT NULL,";
        sql += "tabs TEXT NOT NULL,";
        sql += "times_opened INTEGER NOT NULL,";
        sql += "last_access DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,";
        sql += "last_change DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        SQLiteDriver.executeUpdate(sql);
    }

    public static String getAccountTabs(EntityPlayer player, String bankIdentifier) {
        DBPlayer dbPlayer = TablePlayers.getPlayer(player.getGameProfile());
        String tabs = null;
        try (Connection conn = SQLiteDriver.getConnection();) {
            String sqlUpdate = "UPDATE bank_accounts SET times_opened = times_opened + 1, last_access=datetime('now') WHERE bank_identifier=? AND player_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, bankIdentifier);
                ps.setInt(2, dbPlayer.getId());
                ps.executeUpdate();
            }

            String sqlGetTabs = "SELECT * FROM bank_accounts WHERE bank_identifier=? AND player_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlGetTabs)) {
                ps.setString(1, bankIdentifier);
                ps.setInt(2, dbPlayer.getId());
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

    public static int setAccount(EntityPlayer player, String bankIdentifier, String tabs) {
        DBPlayer dbPlayer = TablePlayers.getPlayer(player.getGameProfile());
        int id = -1;
        String sql = "INSERT INTO bank_accounts (id, player_id, bank_identifier, tabs, times_opened, last_access, last_change) VALUES (NULL, ?, ?, ?, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dbPlayer.getId());
            ps.setString(2, bankIdentifier);
            ps.setString(3, tabs);
            id = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static void updateAccount(EntityPlayer player, String bankIdentifier, String tabs) {
        DBPlayer dbPlayer = TablePlayers.getPlayer(player.getGameProfile());
        String sql = "UPDATE bank_accounts SET tabs=?, last_access=datetime('now'), last_change=datetime('now') WHERE player_id=? AND bank_identifier=?";
        try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tabs);
            ps.setInt(2, dbPlayer.getId());
            ps.setString(3, bankIdentifier);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAccountInDatabase(EntityPlayer player, String bankIdentifier) {
        boolean foundAccount = false;
        DBPlayer dbPlayer = TablePlayers.getPlayer(player.getGameProfile());
        String sql = "SELECT * FROM bank_accounts WHERE bank_identifier=? AND player_id=?";
        try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bankIdentifier);
            ps.setInt(2, dbPlayer.getId());
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
