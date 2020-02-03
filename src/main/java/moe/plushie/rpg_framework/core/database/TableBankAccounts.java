package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import moe.plushie.rpg_framework.api.core.IIdentifier;

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
        DatabaseManager.executeUpdate(DatebaseTable.BANK_ACCOUNTS, sql);
    }

    public static String getAccountTabs(DBPlayer dbPlayer, IIdentifier bankIdentifier) {
        String tabs = null;
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.BANK_ACCOUNTS);) {
            String sqlUpdate = "UPDATE bank_accounts SET times_opened = times_opened + 1, last_access=datetime('now') WHERE bank_identifier=? AND player_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setObject(1, bankIdentifier.getValue());
                ps.setInt(2, dbPlayer.getId());
                ps.executeUpdate();
            }

            String sqlGetTabs = "SELECT * FROM bank_accounts WHERE bank_identifier=? AND player_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlGetTabs)) {
                ps.setObject(1, bankIdentifier.getValue());
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

    public static int setAccount(DBPlayer dbPlayer, IIdentifier bankIdentifier, String tabs) {
        int id = -1;
        String sql = "INSERT INTO bank_accounts (id, player_id, bank_identifier, tabs, times_opened, last_access, last_change) VALUES (NULL, ?, ?, ?, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.BANK_ACCOUNTS); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dbPlayer.getId());
            ps.setObject(2, bankIdentifier.getValue());
            ps.setString(3, tabs);
            id = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static void updateAccount(DBPlayer dbPlayer, IIdentifier bankIdentifier, String tabs) {
        String sql = "UPDATE bank_accounts SET tabs=?, last_access=datetime('now'), last_change=datetime('now') WHERE player_id=? AND bank_identifier=?";
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.BANK_ACCOUNTS); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tabs);
            ps.setInt(2, dbPlayer.getId());
            ps.setObject(3, bankIdentifier.getValue());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAccountInDatabase(DBPlayer dbPlayer, IIdentifier bankIdentifier) {
        boolean foundAccount = false;
        String sql = "SELECT * FROM bank_accounts WHERE bank_identifier=? AND player_id=?";
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.BANK_ACCOUNTS); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, bankIdentifier.getValue());
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
