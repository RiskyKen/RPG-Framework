package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import moe.plushie.rpg_framework.core.database.driver.IDatabaseDriver;
import moe.plushie.rpg_framework.core.database.driver.SQLiteDriver;

public final class DatabaseManager {
    
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);
    private static final IDatabaseDriver DATABASE_DRIVER = new SQLiteDriver();

    private DatabaseManager() {
    }

    public static Connection getConnection(DatebaseTable table) throws SQLException {
        return DATABASE_DRIVER.getConnection(table);
    }

    @Deprecated
    public static Connection getConnection() throws SQLException {
        return DATABASE_DRIVER.getConnection(DatebaseTable.RPG);
    }

    public static PreparedStatement getPreparedStatement(DatebaseTable table, String sql) {
        return DATABASE_DRIVER.getPreparedStatement(table, sql);
    }

    public static void executeUpdate(DatebaseTable table, String sql) {
        DATABASE_DRIVER.executeUpdate(table, sql);
    }

    @Deprecated
    public static void executeUpdate(String sql) {
        DATABASE_DRIVER.executeUpdate(DatebaseTable.RPG, sql);
    }

    public static void executeUpdate(DatebaseTable table, String... sql) {
        DATABASE_DRIVER.executeUpdate(table, sql);
    }

    @Deprecated
    public static void executeUpdate(String... sql) {
        DATABASE_DRIVER.executeUpdate(DatebaseTable.RPG, sql);
    }

    public static ArrayList<String> executeQueryArrayList(DatebaseTable table, String sql) {
        return DATABASE_DRIVER.executeQueryArrayList(table, sql);
    }

    @Deprecated
    public static ArrayList<String> executeQueryArrayList(String sql) {
        return DATABASE_DRIVER.executeQueryArrayList(DatebaseTable.RPG, sql);
    }

    public static int getLastInsertRow(Connection conn) throws SQLException {
        return DATABASE_DRIVER.getLastInsertRow(conn);
    }
}
