package moe.plushie.rpg_framework.core.common.database.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.PooledConnection;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import moe.plushie.rpg_framework.core.common.config.ConfigStorage;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;

public class MySqlDriver implements IDatabaseDriver {

    private PooledConnection pooledConnection = null;

    private final ISqlBulder sqlBulder;

    public MySqlDriver() {
        sqlBulder = new MySqlBuilder();
        createDatabase();
    }

    private String getConnectionUrl(boolean database) {
        String host = ConfigStorage.getMySqlHost() + ":" + ConfigStorage.getMySqlPort();
        if (database) {
            return "jdbc:mysql://" + host + "/" + getDatabaseName();
        } else {
            return "jdbc:mysql://" + host + "/";
        }
    }

    private boolean createDatabase() {
        try (Connection connection = DriverManager.getConnection(getConnectionUrl(false), getUsername(), getPassword()); Statement statement = connection.createStatement()) {
            String sql = "CREATE DATABASE IF NOT EXISTS " + getDatabaseName();
            statement.setQueryTimeout(10);
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String getUsername() {
        return ConfigStorage.getMySqlUsername();
    }

    private String getPassword() {
        return ConfigStorage.getMySqlPassword();
    }

    private String getDatabaseName() {
        return ConfigStorage.getMySqlDatabase();
    }

    private PooledConnection makePool() throws SQLException {
        MysqlConnectionPoolDataSource poolDataSource = new MysqlConnectionPoolDataSource();
        poolDataSource.setServerName(ConfigStorage.getMySqlHost());
        poolDataSource.setPort(ConfigStorage.getMySqlPort());
        poolDataSource.setDatabaseName(ConfigStorage.getMySqlDatabase());
        poolDataSource.setUser(ConfigStorage.getMySqlUsername());
        poolDataSource.setPassword(ConfigStorage.getMySqlPassword());
        return poolDataSource.getPooledConnection();
    }

    private Connection getPoolConnection(DatebaseTable table) throws SQLException {
        if (pooledConnection == null) {
            pooledConnection = makePool();
        }
        return pooledConnection.getConnection();
    }

    @Override
    public synchronized Connection getConnection(DatebaseTable table) throws SQLException {
        Connection connection = null;
        connection = getPoolConnection(table);
        // connection = DriverManager.getConnection(getConnectionUrl(true), getUsername(), getPassword());
        return connection;
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

    @Override
    public int getLastInsertRow(Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ISqlBulder getSqlBulder() {
        return sqlBulder;
    }
}
