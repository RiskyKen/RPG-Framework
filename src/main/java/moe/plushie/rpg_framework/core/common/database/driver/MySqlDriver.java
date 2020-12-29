package moe.plushie.rpg_framework.core.common.database.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.PooledConnection;

import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;

public class MySqlDriver implements IDatabaseDriver {
    
    private static final String DATABASE_NAME = LibModInfo.ID;
    private PooledConnection pooledConnection = null;
    
    private final ISqlBulder sqlBulder;

    public MySqlDriver() {
        sqlBulder = new MySqlBuilder();
    }
    
    private String getConnectionUrl() {
        return "jdbc:mysql://localhost:3306/" + DATABASE_NAME;
    }
    
    private PooledConnection makePool() throws SQLException {
        return null;
    }
    
    @Override
    public synchronized Connection getConnection(DatebaseTable table) throws SQLException {
        Connection connection = null;
        // connection = getPoolConnection(table);
         connection = DriverManager.getConnection(getConnectionUrl(), "root", "root");

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
