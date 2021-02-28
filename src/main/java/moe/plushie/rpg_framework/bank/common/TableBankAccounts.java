package moe.plushie.rpg_framework.bank.common;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.config.ConfigStorage;
import moe.plushie.rpg_framework.core.common.config.ConfigStorage.StorageType;
import moe.plushie.rpg_framework.core.common.database.DBPlayer;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.driver.MySqlBuilder;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder.ISqlBulderCreateTable;

public final class TableBankAccounts {

    private final static String TABLE_NAME = "bank_accounts";

    private TableBankAccounts() {
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
        ISqlBulderCreateTable table = sqlBulder.createTable(TABLE_NAME);
        table.addColumn("id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true).setAutoIncrement(true);
        table.addColumn("player_id", ISqlBulder.DataType.INT).setNotNull(true);
        table.addColumn("bank_identifier", ISqlBulder.DataType.TEXT).setNotNull(true);
        table.addColumn("tabs", ISqlBulder.DataType.LONGTEXT).setNotNull(true);
        table.addColumn("times_opened", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true);
        table.addColumn("last_access", ISqlBulder.DataType.DATETIME).setNotNull(true).setDefault("CURRENT_TIMESTAMP");
        table.addColumn("last_change", ISqlBulder.DataType.DATETIME).setNotNull(true).setDefault("CURRENT_TIMESTAMP");
        table.ifNotExists(true);
        table.setPrimaryKey("id");
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(table.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getAccountTabs(DBPlayer dbPlayer, IIdentifier bankIdentifier) {
        String tabs = null;
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA);) {
            String sqlUpdate = "UPDATE `bank_accounts` SET `times_opened` = `times_opened` + 1, `last_access`=datetime('now') WHERE `bank_identifier`=? AND `player_id`=?";
            if (ConfigStorage.getStorageType() == StorageType.MYSQL) {
                sqlUpdate = "UPDATE `bank_accounts` SET `times_opened` = `times_opened` + 1, `last_access`=now() WHERE `bank_identifier`=? AND `player_id`=?";
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setObject(1, bankIdentifier.getValue());
                ps.setInt(2, dbPlayer.getId());
                ps.executeUpdate();
            }

            String sqlGetTabs = "SELECT * FROM `bank_accounts` WHERE `bank_identifier`=? AND `player_id`=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlGetTabs)) {
                ps.setObject(1, bankIdentifier.getValue());
                ps.setInt(2, dbPlayer.getId());
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    tabs = resultSet.getString("tabs");
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tabs;
    }

    public static int setAccount(DBPlayer dbPlayer, IIdentifier bankIdentifier, String tabs) {
        int id = -1;
        String sql = "INSERT INTO bank_accounts (id, player_id, bank_identifier, tabs, times_opened, last_access, last_change) VALUES (NULL, ?, ?, ?, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dbPlayer.getId());
            ps.setObject(2, bankIdentifier.getValue());
            ps.setString(3, tabs);
            id = ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static void updateAccount(DBPlayer dbPlayer, IIdentifier bankIdentifier, String tabs) {
        String sql = "UPDATE `bank_accounts` SET `tabs`=?, `last_access`=datetime('now'), `last_change`=datetime('now') WHERE `player_id`=? AND `bank_identifier`=?";
        if (ConfigStorage.getStorageType() == StorageType.MYSQL) {
            sql = "UPDATE `bank_accounts` SET `tabs`=?, `last_access`=now(), `last_change`=now() WHERE `player_id`=? AND `bank_identifier`=?";
        }
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tabs);
            ps.setInt(2, dbPlayer.getId());
            ps.setObject(3, bankIdentifier.getValue());
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAccountInDatabase(DBPlayer dbPlayer, IIdentifier bankIdentifier) {
        boolean foundAccount = false;
        String sql = "SELECT * FROM `bank_accounts` WHERE `bank_identifier`=? AND `player_id`=?";
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, bankIdentifier.getValue());
            ps.setInt(2, dbPlayer.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                foundAccount = true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundAccount;
    }

    public static void importData(ArrayList<DBBankAccount> bankAccounts, Connection connection, boolean dropTable) {
        if (dropTable) {
            String sqlDrop = "DROP TABLE IF EXISTS bank_accounts";
            try (PreparedStatement ps = connection.prepareStatement(sqlDrop)) {
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            create(connection, new MySqlBuilder());
        }

        for (DBBankAccount bankAccount : bankAccounts) {
            try (PreparedStatement ps = connection.prepareStatement("ALTER TABLE bank_accounts AUTO_INCREMENT=" + bankAccount.getId())) {
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String sql = "INSERT INTO bank_accounts (id, player_id, bank_identifier, tabs, times_opened, last_access, last_change) VALUES (NULL, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, bankAccount.getPlayer().getId());
                ps.setObject(2, bankAccount.getBankIdentifier().getValue());
                ps.setString(3, bankAccount.getTabs());
                ps.setInt(4, bankAccount.getTimesOpened());
                ps.setDate(5, bankAccount.getLastAccess());
                ps.setDate(6, bankAccount.getLastChanged());
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<DBBankAccount> exportData(Connection connection) {
        ArrayList<DBBankAccount> bankAccounts = new ArrayList<DBBankAccount>();
        String sql = "SELECT * FROM bank_accounts";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    DBPlayer player = new DBPlayer(resultSet.getInt("player_id"));
                    IIdentifier bankIdentifier = new IdentifierString(resultSet.getString("bank_identifier"));
                    String tabs = resultSet.getString("tabs");
                    int timesOpened = resultSet.getInt("times_opened");
                    java.sql.Date lastAccess = resultSet.getDate("last_access");
                    java.sql.Date lastChanged = resultSet.getDate("last_change");
                    bankAccounts.add(new DBBankAccount(id, player, bankIdentifier, tabs, timesOpened, lastAccess, lastChanged));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankAccounts;
    }

    public static class DBBankAccount {

        private final int id;
        private final DBPlayer player;
        private final IIdentifier bankIdentifier;
        private final String tabs;
        private final int timesOpened;
        private final java.sql.Date lastAccess;
        private final java.sql.Date lastChanged;

        public DBBankAccount(int id, DBPlayer player, IIdentifier bankIdentifier, String tabs, int timesOpened, Date lastAccess, Date lastChanged) {
            this.id = id;
            this.player = player;
            this.bankIdentifier = bankIdentifier;
            this.tabs = tabs;
            this.timesOpened = timesOpened;
            this.lastAccess = lastAccess;
            this.lastChanged = lastChanged;
        }

        public int getId() {
            return id;
        }

        public DBPlayer getPlayer() {
            return player;
        }

        public IIdentifier getBankIdentifier() {
            return bankIdentifier;
        }

        public String getTabs() {
            return tabs;
        }

        public int getTimesOpened() {
            return timesOpened;
        }

        public java.sql.Date getLastAccess() {
            return lastAccess;
        }

        public java.sql.Date getLastChanged() {
            return lastChanged;
        }
    }
}
