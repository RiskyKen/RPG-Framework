package moe.plushie.rpg_framework.shop.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.JsonArray;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.shop.IShop;
import moe.plushie.rpg_framework.api.shop.IShop.IShopTab;
import moe.plushie.rpg_framework.core.common.IdentifierInt;
import moe.plushie.rpg_framework.core.common.config.ConfigStorage;
import moe.plushie.rpg_framework.core.common.config.ConfigStorage.StorageType;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder.ISqlBulderCreateTable;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.shop.common.serialize.ShopSerializer;

public final class TableShops {
    
    private final static String TABLE_NAME = "shops";
    
    private TableShops() {
    }
    
    private static DatebaseTable getDatebaseTable() {
        return DatebaseTable.DATA;
    }
    
    private static Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

    public static void create() {
        ISqlBulderCreateTable table = DatabaseManager.getSqlBulder().createTable(TABLE_NAME);
        table.addColumn("id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true).setAutoIncrement(true);
        table.addColumn("name", ISqlBulder.DataType.VARCHAR).setSize(80).setNotNull(true);
        table.addColumn("tabs", ISqlBulder.DataType.TEXT).setNotNull(true);
        table.addColumn("last_update", ISqlBulder.DataType.DATETIME).setNotNull(true).setDefault("CURRENT_TIMESTAMP");
        table.ifNotExists(true);
        table.setPrimaryKey("id");
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(table.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
//        String sql = "CREATE TABLE IF NOT EXISTS shops";
//        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
//        sql += "name VARCHAR(80) NOT NULL,";
//        sql += "tabs TEXT NOT NULL,";
//        sql += "last_update DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
//        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
//            statement.executeUpdate(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    private static final String SQL_ADD_SHOP = "INSERT INTO shops (id, name, tabs, last_update) VALUES (NULL, ?, ?, CURRENT_TIMESTAMP)";

    public static void createNewShop(String name) {
        //IShop shop = null;
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_ADD_SHOP)) {
            ps.setString(1, name);
            ps.setObject(2, "[]");
            ps.executeUpdate();
            //int row = DatabaseManager.getLastInsertRow(conn);
            //shop = new Shop(new IdentifierInt(row), name);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return shop;
    }

    public static void addNewShop(Shop shop) {
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_ADD_SHOP)) {
            ps.setString(1, shop.getName());
            ps.setString(2, ShopSerializer.serializeTabs(shop.getTabs(), false).toString());
            ps.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String SQL_DELETE_SHOP = "DELETE FROM shops WHERE id=?";

    public static void deleteShop(IIdentifier identifier) {
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_DELETE_SHOP)) {
            ps.setObject(1, identifier.getValue());
            ps.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String SQL_GET_SHOP = "SELECT name, tabs FROM shops WHERE id=?";

    public static IShop getShop(IIdentifier identifier) {
        IShop shop = null;
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_GET_SHOP)) {
            ps.setObject(1, identifier.getValue());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                JsonArray tabsJson = SerializeHelper.stringToJson(resultSet.getString("tabs")).getAsJsonArray();
                ArrayList<IShopTab> shopTabs = ShopSerializer.deserializeTabs(tabsJson);
                shop = new Shop(identifier, resultSet.getString("name"), shopTabs);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shop;
    }

    private static final String SQL_GET_SHOP_LIST = "SELECT id, name, last_update FROM shops";

    public static void getShopList(ArrayList<IIdentifier> identifiers, ArrayList<String> names, ArrayList<Date> dates) {
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_GET_SHOP_LIST)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                if (identifiers != null) {
                    identifiers.add(new IdentifierInt(resultSet.getInt("id")));
                }
                if (names != null) {
                    names.add(resultSet.getString("name"));
                }
                if (dates != null) {
                    dates.add(resultSet.getDate("last_update"));
                }
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateShop(IShop shop) {
        String sql = "UPDATE shops SET `name`=?, `tabs`=?, `last_update`=datetime('now') WHERE `id`=?";
        if (ConfigStorage.getStorageType() == StorageType.MYSQL) {
            sql = "UPDATE shops SET `name`=?, `tabs`=?, `last_update`=now() WHERE `id`=?";
        }
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shop.getName());
            ps.setString(2, ShopSerializer.serializeTabs(shop.getTabs(), false).toString());
            ps.setObject(3, shop.getIdentifier().getValue());
            ps.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
