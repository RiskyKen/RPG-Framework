package moe.plushie.rpg_framework.core.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import javax.sql.PooledConnection;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.Pragma;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import moe.plushie.rpg_framework.core.RPGFramework;

public final class SQLiteDriver {

    private static final String FILE_NAME = "rpg.sqlite3";
    private static PooledConnection pooledConnection;

    private static String getConnectionUrl() {
        File file = new File(RPGFramework.getProxy().getModDirectory(), FILE_NAME);
        return "jdbc:sqlite:" + file.getAbsolutePath();
    }

    private static PooledConnection makePool() throws SQLException {
        SQLiteConfig config = makeConfig();
        SQLiteConnectionPoolDataSource ds = new SQLiteConnectionPoolDataSource();
        ds.setUrl(getConnectionUrl());
        ds.setConfig(config);
        return ds.getPooledConnection();
    }

    public static Connection getPoolConnection() throws SQLException {
        if (pooledConnection == null) {
            pooledConnection = makePool();
        }
        return pooledConnection.getConnection();
    }

    public static Connection getConnection() throws SQLException {
        return getPoolConnection();
        // return DriverManager.getConnection(getConnectionUrl(), makeConfig().toProperties());
    }

    public static PreparedStatement getPreparedStatement(String sql) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SQLiteConfig makeConfig() {
        SQLiteConfig config = new SQLiteConfig(makeProperties());
        config.setPragma(Pragma.DATE_STRING_FORMAT, "yyyy-MM-dd HH:mm:ss");
        config.setPragma(Pragma.JOURNAL_MODE, "WAL");
        config.setPragma(Pragma.SYNCHRONOUS, "NORMAL");
        return config;
    }

    private static Properties makeProperties() {
        Properties properties = new SQLiteConfig().toProperties();
        properties.setProperty(Pragma.DATE_STRING_FORMAT.pragmaName, "yyyy-MM-dd HH:mm:ss");
        properties.setProperty(Pragma.JOURNAL_MODE.pragmaName, "WAL");
        properties.setProperty(Pragma.SYNCHRONOUS.pragmaName, "NORMAL");
        return properties;
    }

    public static void executeUpdate(String sql) {
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.setQueryTimeout(10);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void executeUpdate(String... sql) {
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.setQueryTimeout(10);
            for (String s : sql) {
                statement.executeUpdate(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> executeQueryArrayList(String sql) {
        ArrayList<String> results = new ArrayList<String>();
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
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

    public static int getLastInsertRow(Connection conn) throws SQLException {
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
