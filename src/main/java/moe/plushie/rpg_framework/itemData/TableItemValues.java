package moe.plushie.rpg_framework.itemData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;

public class TableItemValues {

    private final static String TABLE_NAME = "value_items";

    private DatebaseTable getDatebaseTable() {
        return DatebaseTable.DATA;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

    public void create() {
        try (Connection conn = getConnection()) {
            createTableTags(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableTags(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME;
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addColumn(conn, "item_reg_name TEXT DEFAULT '' NOT NULL");
        addColumn(conn, "item_meta INTEGER DEFAULT 0 NOT NULL");
        addColumn(conn, "cost INTEGER DEFAULT '{}' NOT NULL");

        sql = "CREATE INDEX IF NOT EXISTS idx_item_reg_name ON " + TABLE_NAME + " (item_reg_name)";
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
