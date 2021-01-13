package moe.plushie.rpg_framework.currency.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.database.DBPlayer;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.TablePlayers;
import moe.plushie.rpg_framework.core.common.database.driver.MySqlBuilder;
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
        try (Connection connection = getConnection()) {
            create(connection, DatabaseManager.getSqlBulder());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void create(Connection connection, ISqlBulder sqlBulder) {
//      String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME;
//      sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
//      sql += "player_id INTEGER NOT NULL,";
//      sql += "currency_identifier TEXT NOT NULL,";
//      sql += "amount INTEGER NOT NULL,";
//      sql += "times_opened INTEGER NOT NULL,";
//      sql += "last_change DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
//      try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
//          statement.executeUpdate(sql);
//      } catch (SQLException e) {
//          e.printStackTrace();
//      }

        ISqlBulderCreateTable table = sqlBulder.createTable(TABLE_NAME);
        table.addColumn("id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true).setAutoIncrement(true);
        table.addColumn("player_id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true);
        table.addColumn("currency_identifier", ISqlBulder.DataType.TEXT).setNotNull(true);
        table.addColumn("amount", ISqlBulder.DataType.INT).setNotNull(true);
        table.addColumn("times_opened", ISqlBulder.DataType.INT).setNotNull(true);
        table.addColumn("last_change", ISqlBulder.DataType.DATETIME).setNotNull(true).setDefault("CURRENT_TIMESTAMP");
        table.ifNotExists(true);
        table.setPrimaryKey("id");
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(table.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveWallet(GameProfile gameProfile, IWallet wallet) {
        try (Connection conn = getConnection()) {
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

    public static void loadWallet(GameProfile gameProfile, IWallet wallet) {
        try (Connection conn = getConnection()) {
            DBPlayer dbPlayer = TablePlayers.getPlayer(conn, gameProfile);
            if (dbPlayer.isMissing()) {
                RPGFramework.getLogger().info("Tried to get missing players wallet from the DB. " + gameProfile.toString());
                return;
            }
            if (isWalletInDatabase(conn, dbPlayer, wallet.getCurrency().getIdentifier())) {
                String sql = "SELECT * FROM wallets WHERE currency_identifier=? AND player_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setObject(1, wallet.getCurrency().getIdentifier().getValue());
                    ps.setInt(2, dbPlayer.getId());
                    try (ResultSet resultSet = ps.executeQuery()) {
                        if (resultSet.next()) {
                            wallet.setAmount(resultSet.getInt("amount"));
                        }
                    }
                }
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
        String sql = "UPDATE wallets SET amount=?, last_change=datetime('now') WHERE player_id=? AND currency_identifier=?";
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

    public static void importData(ArrayList<DBWallet> wallets, Connection connection, boolean dropTable) {
        if (dropTable) {
            String sqlDrop = "DROP TABLE IF EXISTS wallets";
            try (PreparedStatement ps = connection.prepareStatement(sqlDrop)) {
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            create(connection, new MySqlBuilder());
        }

        for (DBWallet wallet : wallets) {
            try (PreparedStatement ps = connection.prepareStatement("ALTER TABLE wallets AUTO_INCREMENT=" + wallet.getId())) {
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String sql = "INSERT INTO wallets (id, player_id, currency_identifier, amount, times_opened, last_change) VALUES (NULL, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, wallet.getPlayer().getId());
                ps.setObject(2, wallet.getCurrencyIdentifier().getValue());
                ps.setInt(3, wallet.getAmount());
                ps.setInt(4, wallet.getTimesOpened());
                ps.setDate(5, wallet.getLastChanged());

                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<DBWallet> exportData(Connection connection) {
        ArrayList<DBWallet> wallets = new ArrayList<DBWallet>();
        String sql = "SELECT * FROM wallets";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    DBPlayer player = new DBPlayer(resultSet.getInt("player_id"));
                    IIdentifier currencyIdentifier = new IdentifierString(resultSet.getString("currency_identifier"));
                    int amount = resultSet.getInt("amount");
                    int timesOpened = resultSet.getInt("times_opened");
                    java.sql.Date lastChanged = resultSet.getDate("last_change");
                    wallets.add(new DBWallet(id, player, currencyIdentifier, amount, timesOpened, lastChanged));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wallets;
    }

    public static class DBWallet {

        private final int id;
        private final DBPlayer player;
        private final IIdentifier currencyIdentifier;
        private final int amount;
        private final int timesOpened;
        private final java.sql.Date lastChanged;

        public DBWallet(int id, DBPlayer player, IIdentifier currencyIdentifier, int amount, int timesOpened, java.sql.Date lastChanged) {
            this.id = id;
            this.player = player;
            this.currencyIdentifier = currencyIdentifier;
            this.amount = amount;
            this.timesOpened = timesOpened;
            this.lastChanged = lastChanged;
        }

        public int getId() {
            return id;
        }

        public DBPlayer getPlayer() {
            return player;
        }

        public IIdentifier getCurrencyIdentifier() {
            return currencyIdentifier;
        }

        public int getAmount() {
            return amount;
        }

        public int getTimesOpened() {
            return timesOpened;
        }

        public java.sql.Date getLastChanged() {
            return lastChanged;
        }
    }
}
