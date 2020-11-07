package moe.plushie.rpg_framework.core.common.database.driver;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.PooledConnection;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.JournalMode;
import org.sqlite.SQLiteConfig.LockingMode;
import org.sqlite.SQLiteConfig.Pragma;
import org.sqlite.SQLiteConfig.SynchronousMode;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;

public final class SQLiteDriver implements IDatabaseDriver {

    private static final String FILE_EXTENSION = ".sqlite3";

    private final ConcurrentHashMap<DatebaseTable, PooledConnection> pooledConnections;
    private final ISqlBulder sqlBulder;

    public SQLiteDriver() {
        pooledConnections = new ConcurrentHashMap<DatebaseTable, PooledConnection>();
        sqlBulder = new SQLiteBuilder();
    }

    public static File getDatabaseFile(DatebaseTable table) {
        return new File(RPGFramework.getProxy().getModDirectory(), table.name().toLowerCase() + FILE_EXTENSION);
    }

    private String getConnectionUrl(DatebaseTable table) {
        return "jdbc:sqlite:" + getDatabaseFile(table).getAbsolutePath();
    }

    private PooledConnection makePool(DatebaseTable table) throws SQLException {
        SQLiteConfig config = makeConfig();
        SQLiteConnectionPoolDataSource ds = new SQLiteConnectionPoolDataSource(config);
        ds.setUrl(getConnectionUrl(table));
        ds.setConfig(config);
        return ds.getPooledConnection();
    }

    private Connection getPoolConnection(DatebaseTable table) throws SQLException {
        synchronized (pooledConnections) {
            if (!pooledConnections.containsKey(table)) {
                pooledConnections.put(table, makePool(table));
            }
        }
        return pooledConnections.get(table).getConnection();
    }

    @Override
    public Connection getConnection(DatebaseTable table) throws SQLException {
        Connection connection = null;

        connection = getPoolConnection(table);
        // connection = DriverManager.getConnection(getConnectionUrl(table), makeConfig().toProperties());

        return connection;
    }

    private SQLiteConfig makeConfig() {
        SQLiteConfig config = new SQLiteConfig(makeProperties());
        config.setJournalMode(JournalMode.WAL);
        config.setSynchronous(SynchronousMode.NORMAL);
        config.setDateStringFormat("yyyy-MM-dd HH:mm:ss");
        config.setPragma(Pragma.DATE_STRING_FORMAT, "yyyy-MM-dd HH:mm:ss");
        config.setLockingMode(LockingMode.NORMAL);
        return config;
    }

    private Properties makeProperties() {
        Properties properties = new SQLiteConfig().toProperties();
        properties.setProperty(Pragma.DATE_STRING_FORMAT.pragmaName, "yyyy-MM-dd HH:mm:ss");
        properties.setProperty(Pragma.JOURNAL_MODE.pragmaName, JournalMode.WAL.toString());
        properties.setProperty(Pragma.SYNCHRONOUS.pragmaName, SynchronousMode.NORMAL.toString());
        return properties;
    }

    @Override
    public void executeUpdate(DatebaseTable table, String sql) {
        try (Connection conn = getConnection(table); Statement statement = conn.createStatement()) {
            statement.setQueryTimeout(10);
            statement.executeUpdate(sql);
            conn.close();
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
            conn.close();
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
            conn.close();
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

    @Override
    public ISqlBulder getSqlBulder() {
        return sqlBulder;
    }
}
