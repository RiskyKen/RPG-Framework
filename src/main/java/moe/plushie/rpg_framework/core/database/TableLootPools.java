package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.loot.ILootTableItem;
import moe.plushie.rpg_framework.api.loot.ILootTablePool;
import moe.plushie.rpg_framework.core.common.IdentifierInt;
import moe.plushie.rpg_framework.core.common.serialize.ItemStackSerialize;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.loot.common.LootTableItem;
import moe.plushie.rpg_framework.loot.common.LootTablePool;
import net.minecraft.item.ItemStack;

public final class TableLootPools {

    private TableLootPools() {
    }
    
    private static DatebaseTable getDatebaseTable() {
        return DatebaseTable.DATA;
    }
    
    private static Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS loot_pools"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "name VARCHAR(64) NOT NULL,"
            + "category VARCHAR(64) NOT NULL,"
            + "items TEXT NOT NULL,"
            + "last_update DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)";
    
    public static void createTable() {
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(SQL_CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String SQL_ADD_LOOT_POOL = "INSERT INTO loot_pools (id, name, category, items, last_update) VALUES (NULL, ?, ?, ?, CURRENT_TIMESTAMP)";

    public static ILootTablePool createNew(String name, String category) {
        createTable();
        ILootTablePool tablePool = null;
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_ADD_LOOT_POOL)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, "[]");
            ps.executeUpdate();
            int row = DatabaseManager.getLastInsertRow(conn);
            tablePool = new LootTablePool(new IdentifierInt(row), name, category);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tablePool;
    }
    
    private static final String SQL_DELETE_LOOT_POOL = "DELETE FROM loot_pools WHERE id=?";

    public static void delete(IIdentifier identifier) {
        createTable();
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_DELETE_LOOT_POOL)) {
            ps.setObject(1, identifier.getValue());
            ps.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static final String SQL_GET_LOOT_POOL = "SELECT name, category, items FROM loot_pools WHERE id=?";

    public static ILootTablePool get(IIdentifier identifier) {
        createTable();
        ILootTablePool tablePool = null;
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_GET_LOOT_POOL)) {
            ps.setObject(1, identifier.getValue());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                JsonArray itemsJson = SerializeHelper.stringToJson(resultSet.getString("items")).getAsJsonArray();
                ArrayList<ILootTableItem> items = new ArrayList<ILootTableItem>();
                Gson gson = new GsonBuilder().registerTypeAdapter(ItemStack.class, new ItemStackSerialize()).create();
                for (JsonElement json : itemsJson) {
                    items.add(gson.fromJson(json, LootTableItem.class));
                }
                tablePool = new LootTablePool(identifier, resultSet.getString("name"), resultSet.getString("category"), items);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tablePool;
    }
    
    private static final String SQL_UPDATE_LOOT_POOL = "UPDATE loot_pools SET name=?, category=?, items=?, last_update=datetime('now') WHERE id=?";

    public static void update(ILootTablePool tablePool) {
        createTable();
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_LOOT_POOL)) {
            ps.setString(1, tablePool.getName());
            ps.setString(2, tablePool.getCategory());
            JsonArray itemsJson = new JsonArray();
            Gson gson = new GsonBuilder().registerTypeAdapter(ItemStack.class, new ItemStackSerialize()).create();
            for (ILootTableItem item : tablePool.getPoolItems()) {
                itemsJson.add(gson.toJsonTree(item, LootTableItem.class));
            }
            ps.setString(3, itemsJson.toString());
            ps.setObject(4, tablePool.getIdentifier().getValue());
            ps.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static final String SQL_GET_LOOT_POOLS_LIST = "SELECT id, name, category, last_update FROM loot_pools";

    public static void getList(ArrayList<IIdentifier> identifiers, ArrayList<String> names, ArrayList<String> categories, ArrayList<Date> dates) {
        createTable();
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_GET_LOOT_POOLS_LIST)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                if (identifiers != null) {
                    identifiers.add(new IdentifierInt(resultSet.getInt("id")));
                }
                if (names != null) {
                    names.add(resultSet.getString("name"));
                }
                if (categories != null) {
                    categories.add(resultSet.getString("category"));
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

    private static final String SQL_RENAME_LOOT_POOL = "UPDATE loot_pools SET name=?, category=?, last_update=datetime('now') WHERE id=?";
    
    public static void rename(IIdentifier identifier, String name, String category) {
        createTable();
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.DATA); PreparedStatement ps = conn.prepareStatement(SQL_RENAME_LOOT_POOL)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setObject(3, identifier.getValue());
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
