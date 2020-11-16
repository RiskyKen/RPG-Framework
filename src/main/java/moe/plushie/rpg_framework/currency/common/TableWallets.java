package moe.plushie.rpg_framework.currency.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.database.DBPlayer;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.TablePlayers;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder.ISqlBulderCreateTable;

public final class TableWallets {

    private final static String TABLE_NAME = "wallets";

    private TableWallets() {
    }

    private static DatebaseTable getDatebaseTable() {
        return DatebaseTable.PLAYER_DATA;
    }

    private static Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

    public static void create() {
        ISqlBulderCreateTable table = DatabaseManager.getSqlBulder().createTable(TABLE_NAME);
        table.ifNotExists(true);
        table.addColumn("id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true).setAutoIncrement(true);
        table.addColumn("player_id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true);
        table.addColumn("currency_identifier", ISqlBulder.DataType.TEXT).setNotNull(true);
        table.addColumn("amount", ISqlBulder.DataType.INT).setNotNull(true);
        table.addColumn("times_opened", ISqlBulder.DataType.INT).setNotNull(true);
        table.addColumn("last_change", ISqlBulder.DataType.DATETIME).setNotNull(true).setDefault("CURRENT_TIMESTAMP");
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(table.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveWallet(GameProfile gameProfile, IWallet wallet) {
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA)) {
            DBPlayer dbPlayer = TablePlayers.getPlayer(conn, gameProfile);
            if (dbPlayer.isMissing()) {
                RPGFramework.getLogger().info("Tried to add missing players wallet to the DB. " + gameProfile.toString());
                return;
            }
            if (isWalletInDatabase(conn, dbPlayer, wallet.getCurrency().getIdentifier())) {
                updateWallet(conn, dbPlayer, wallet.getCurrency().getIdentifier(), wallet.getAmount());

            } else {
                setWallet(conn, dbPlayer, wallet.getCurrency().getIdentifier(), wallet.getAmount());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int setWallet(Connection conn, DBPlayer dbPlayer, IIdentifier identifier, int value) throws SQLException {
        int id = -1;
        String sql = "INSERT INTO wallets (id, player_id, currency_identifier, amount, times_opened, last_change) VALUES (NULL, ?, ?, ?, 0, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dbPlayer.getId());
            ps.setObject(2, identifier.getValue());
            ps.setInt(3, value);
            id = ps.executeUpdate();
            ps.close();
        }
        return id;
    }

    private static void updateWallet(Connection conn, DBPlayer dbPlayer, IIdentifier identifier, int value) throws SQLException {
        String sql = "UPDATE wallets SET amount=?, last_access=datetime('now') WHERE player_id=? AND currency_identifier=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, value);
            ps.setInt(2, dbPlayer.getId());
            ps.setObject(3, identifier.getValue());
            ps.executeUpdate();
            ps.close();
        }
    }

    private static boolean isWalletInDatabase(Connection conn, DBPlayer dbPlayer, IIdentifier identifier) throws SQLException {
        boolean foundAccount = false;
        String sql = "SELECT * FROM wallets WHERE currency_identifier=? AND player_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, identifier.getValue());
            ps.setInt(2, dbPlayer.getId());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    foundAccount = true;
                }
            }
        }
        return foundAccount;
    }
}
