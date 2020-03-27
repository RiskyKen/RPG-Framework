package moe.plushie.rpg_framework.itemData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;

public final class TableTagValues {

    private final static String TABLE_TAGS_NAME = "value_tags";

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

    private static void createTableTags(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_TAGS_NAME;
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        sql += "tags TEXT NOT NULL)";

        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        sql = "CREATE INDEX IF NOT EXISTS idx_tags ON " + TABLE_TAGS_NAME + " (tags)";;
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
