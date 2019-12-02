package moe.plushie.rpg_framework.core.database.driver;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.sql.PooledConnection;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.Pragma;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.database.DatebaseTable;

public final class SQLiteDriver implements IDatabaseDriver {

    private static final String FILE_EXTENSION = ".sqlite3";
    private final HashMap<String, PooledConnection> pooledConnections = new HashMap<String, PooledConnection>();

    private String getConnectionUrl(String fileName) {
        File file = new File(RPGFramework.getProxy().getModDirectory(), fileName + FILE_EXTENSION);
        return "jdbc:sqlite:" + file.getAbsolutePath();
    }

    private PooledConnection makePool(String fileName) throws SQLException {
        SQLiteConfig config = makeConfig();
        SQLiteConnectionPoolDataSource ds = new SQLiteConnectionPoolDataSource();
        ds.setUrl(getConnectionUrl(fileName));
        ds.setConfig(config);
        return ds.getPooledConnection();
    }

    private Connection getPoolConnection(String fileName) throws SQLException {
        if (!pooledConnections.containsKey(fileName)) {
            pooledConnections.put(fileName, makePool(fileName));
        }
        return pooledConnections.get(fileName).getConnection();
    }

    @Override
    public Connection getConnection(DatebaseTable table) throws SQLException {
        return getPoolConnection(table.name().toLowerCase());
        // return DriverManager.getConnection(getConnectionUrl(), makeConfig().toProperties());
    }

    @Override
    public PreparedStatement getPreparedStatement(DatebaseTable table, String sql) {
        try (Connection conn = getConnection(table); PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SQLiteConfig makeConfig() {
        SQLiteConfig config = new SQLiteConfig(makeProperties());
        config.setPragma(Pragma.DATE_STRING_FORMAT, "yyyy-MM-dd HH:mm:ss");
        config.setPragma(Pragma.JOURNAL_MODE, "WAL");
        config.setPragma(Pragma.SYNCHRONOUS, "NORMAL");
        return config;
    }

    private Properties makeProperties() {
        Properties properties = new SQLiteConfig().toProperties();
        properties.setProperty(Pragma.DATE_STRING_FORMAT.pragmaName, "yyyy-MM-dd HH:mm:ss");
        properties.setProperty(Pragma.JOURNAL_MODE.pragmaName, "WAL");
        properties.setProperty(Pragma.SYNCHRONOUS.pragmaName, "NORMAL");
        return properties;
    }

    @Override
    public void executeUpdate(DatebaseTable table, String sql) {
        try (Connection conn = getConnection(table); Statement statement = conn.createStatement()) {
            statement.setQueryTimeout(10);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeUpdate(DatebaseTable table, String... sql) {
        try (Connection conn = getConnection(table); Statement statement = conn.createStatement()) {
            statement.setQueryTimeout(10);
            for (String s : sql) {
                statement.executeUpdate(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<String> executeQueryArrayList(DatebaseTable table, String sql) {
        ArrayList<String> results = new ArrayList<String>();
        try (Connection conn = getConnection(table); Statement statement = conn.createStatement()) {
            statement.setQueryTimeout(10);
            try (ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    String line = rs.getString(1);
                    for (int i = 2; i < rs.getMetaData().getColumnCount() + 1; i++) {
                        line += " - " + rs.getString(i);
                    }
                    results.add(line);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private static final String SQL_LAST_ROW = "SELECT last_insert_rowid()";

    @Override
    public int getLastInsertRow(Connection conn) throws SQLException {
        int row = -1;
        try (Statement statement = conn.createStatement()) {
            try (ResultSet rs = statement.executeQuery(SQL_LAST_ROW)) {
                if (rs.next()) {
                    row = rs.getInt(1);
                }
            }
        }
        return row;
    }
}
