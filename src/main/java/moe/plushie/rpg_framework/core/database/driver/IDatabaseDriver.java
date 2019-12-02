package moe.plushie.rpg_framework.core.database.driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import moe.plushie.rpg_framework.core.database.DatebaseTable;

public interface IDatabaseDriver {

    public Connection getConnection(DatebaseTable table) throws SQLException;

    public PreparedStatement getPreparedStatement(DatebaseTable table, String sql);

    public void executeUpdate(DatebaseTable table, String sql);

    public void executeUpdate(DatebaseTable table, String... sql);

    public ArrayList<String> executeQueryArrayList(DatebaseTable table, String sql);

    public int getLastInsertRow(Connection conn) throws SQLException;
}
