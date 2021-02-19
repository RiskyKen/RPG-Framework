package moe.plushie.rpg_framework.core.common.database.driver;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.PooledConnection;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import moe.plushie.rpg_framework.core.common.config.ConfigStorage;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;

public class MySqlDriver implements IDatabaseDriver {

    private PooledConnection pooledConnection = null;
    private ComboPooledDataSource comboPooledDataSource = null;
    
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
        poolDataSource.setAutoReconnect(true);
        poolDataSource.setAutoReconnectForPools(true);
        return poolDataSource.getPooledConnection();
    }
    
    private ComboPooledDataSource makeComboPooledDataSource() {
        try {
            ComboPooledDataSource cpds = new ComboPooledDataSource();
            cpds.setDriverClass("com.mysql.jdbc.Driver");
            cpds.setJdbcUrl(getConnectionUrl(true));
            cpds.setUser(ConfigStorage.getMySqlUsername());
            cpds.setPassword(ConfigStorage.getMySqlPassword());
            cpds.setTestConnectionOnCheckin(true);
            cpds.setTestConnectionOnCheckout(true);
            cpds.setIdleConnectionTestPeriod(5);
            cpds.setAutomaticTestTable("connection_test");
            return cpds;
        } catch (PropertyVetoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private synchronized Connection getPoolConnection() throws SQLException {
        if (comboPooledDataSource == null) {
            comboPooledDataSource = makeComboPooledDataSource();
        }
        if (pooledConnection == null) {
            //pooledConnection = makePool();
        }
        return comboPooledDataSource.getConnection();
    }

    @Override
    public synchronized Connection getConnection(DatebaseTable table) throws SQLException {
        return getPoolConnection();
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
