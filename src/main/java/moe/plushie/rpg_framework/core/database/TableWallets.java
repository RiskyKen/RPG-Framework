package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.IWallet;

public final class TableWallets {

    private final static String TABLE_NAME = "wallets";

    private TableWallets() {
    }

    public static void create() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME;
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        sql += "player_id INTEGER NOT NULL,";
        sql += "currency_identifier TEXT NOT NULL,";
        sql += "amount INTEGER NOT NULL,";
        sql += "times_opened INTEGER NOT NULL,";
        sql += "last_change DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        DatabaseManager.executeUpdate(DatebaseTable.PLAYER_DATA, sql);
    }

    public static void saveWallet(GameProfile gameProfile, IWallet wallet) {
        DBPlayer dbPlayer = TablePlayers.getPlayer(gameProfile);
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA)) {
            if (isWalletInDatabase(dbPlayer, wallet.getCurrency().getIdentifier())) {
                updateWallet(dbPlayer, wallet.getCurrency().getIdentifier(), wallet.getAmount());
            } else {
                setWallet(dbPlayer, wallet.getCurrency().getIdentifier(), wallet.getAmount());
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int setWallet(DBPlayer dbPlayer, IIdentifier identifier, int value) throws SQLException {
        int id = -1;
        String sql = "INSERT INTO wallets (id, player_id, currency_identifier, amount, times_opened, last_change) VALUES (NULL, ?, ?, ?, 0, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dbPlayer.getId());
            ps.setObject(2, identifier.getValue());
            ps.setInt(3, value);
            id = ps.executeUpdate();
            ps.close();
            conn.close();
        }
        return id;
    }

    public static void updateWallet(DBPlayer dbPlayer, IIdentifier identifier, int value) throws SQLException {
        String sql = "UPDATE wallets SET amount=?, last_access=datetime('now') WHERE player_id=? AND currency_identifier=?";
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, value);
            ps.setInt(2, dbPlayer.getId());
            ps.setObject(3, identifier.getValue());
            ps.executeUpdate();
            ps.close();
            conn.close();
        }
    }

    public static boolean isWalletInDatabase(DBPlayer dbPlayer, IIdentifier identifier) throws SQLException {
        boolean foundAccount = false;
        String sql = "SELECT * FROM wallets WHERE currency_identifier=? AND player_id=?";
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, identifier.getValue());
            ps.setInt(2, dbPlayer.getId());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                foundAccount = true;
            }
            ps.close();
            conn.close();
        }
        return foundAccount;
    }
}
