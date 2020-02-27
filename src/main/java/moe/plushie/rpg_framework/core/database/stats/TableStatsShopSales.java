package moe.plushie.rpg_framework.core.database.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.DatebaseTable;
import net.minecraft.item.ItemStack;

public final class TableStatsShopSales {

    private TableStatsShopSales() {
    }

    private static DatebaseTable getDatebaseTable() {
        return DatebaseTable.STATS;
    }

    private static Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

    public static void create() {
        String sql = "CREATE TABLE IF NOT EXISTS shop_sales";
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        sql += "shop_identifier TEXT NOT NULL,";
        sql += "item TEXT NOT NULL,";
        sql += "amount INTEGER NOT NULL,";
        sql += "date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateSoldItemCount(IIdentifier shop, ItemStack itemStack) {
        String item = SerializeHelper.writeItemToJson(itemStack, false).toString();
        try (Connection conn = getConnection()) {
            if (itemRecordExists(conn, shop, item)) {
                itemRecordUpdate(conn, shop, item);
            } else {
                itemRecordCreate(conn, shop, item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void itemRecordUpdate(Connection conn, IIdentifier shop, String item) throws SQLException {
        String sql = "UPDATE shop_sales SET amount = amount + 1, date=datetime('now') WHERE shop_identifier=? AND item=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, shop.getValue());
            ps.setString(2, item);
            ps.execute();
        }
    }

    private static void itemRecordCreate(Connection conn, IIdentifier shop, String item) throws SQLException {
        String sql = "INSERT INTO shop_sales (id, shop_identifier, item, amount, date) VALUES (NULL, ?, ?, 1, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, shop.getValue());
            ps.setString(2, item);
            ps.execute();
        }
    }

    private static boolean itemRecordExists(Connection conn, IIdentifier shop, String item) throws SQLException {
        boolean exists = false;
        String sql = "SELECT id FROM shop_sales WHERE shop_identifier=? AND item=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, shop.getValue());
            ps.setString(2, item);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                }
            }
        }
        return exists;
    }
}
