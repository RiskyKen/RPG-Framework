package moe.plushie.rpg_framework.itemData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder.ISqlBulderCreateTable;

public final class TableTagValues {

    private final static String TABLE_NAME = "tag_values";

    private DatebaseTable getDatebaseTable() {
        return DatebaseTable.DATA;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

    public void create() {
        ISqlBulderCreateTable table = DatabaseManager.getSqlBulder().createTable(TABLE_NAME);
        table.addColumn("id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true).setAutoIncrement(true);
        table.addColumn("tags", ISqlBulder.DataType.TEXT).setNotNull(true);
        table.ifNotExists(true);
        table.setPrimaryKey("id");
        //table.addKey("idx_tags", false, "tags");
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(table.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
//        try (Connection conn = getConnection()) {
//            createTableTags(conn);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    private void createTableTags(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME;
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addColumn(conn, "tags TEXT DEFAULT '' NOT NULL");

        sql = "CREATE INDEX IF NOT EXISTS idx_tags ON " + TABLE_NAME + " (tags)";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addColumn(Connection conn, String data) {
        try (Statement s = conn.createStatement()) {
            s.execute("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + data);
        } catch (SQLException e) {
            // Column already exists.
        }
    }
}
