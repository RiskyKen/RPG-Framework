package moe.plushie.rpg_framework.itemData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.currency.common.serialize.CostSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class TableItemData {

    private final static String TABLE_ITEMS_NAME = "item_data";

    private DatebaseTable getDatebaseTable() {
        return DatebaseTable.DATA;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

    public void create() {
        try (Connection conn = getConnection()) {
            createTableItems(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableItems(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS_NAME;
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addColumn(conn, "item_reg_name TEXT DEFAULT '' NOT NULL");
        addColumn(conn, "item_meta INTEGER DEFAULT 0 NOT NULL");
        addColumn(conn, "categories TEXT DEFAULT '[]' NOT NULL");
        addColumn(conn, "tags TEXT DEFAULT '[]' NOT NULL");
        addColumn(conn, "cost TEXT DEFAULT '{}' NOT NULL");

        sql = "CREATE INDEX IF NOT EXISTS idx_data_item_reg ON " + TABLE_ITEMS_NAME + " (item_reg_name, item_meta)";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addColumn(Connection conn, String data) {
        try (Statement s = conn.createStatement()) {
            s.execute("ALTER TABLE " + TABLE_ITEMS_NAME + " ADD COLUMN " + data);
        } catch (SQLException e) {
            // Column already exists.
        }
    }

    public IItemData getItemData(ItemStack itemStack) {
        IItemData itemData = ItemData.createEmpty();
        try (Connection conn = getConnection()) {
            if (isValueInDatabase(conn, itemStack.getItem(), (short) itemStack.getMetadata())) {
                itemData = getItemData(conn, itemStack.getItem(), (short) itemStack.getMetadata());
            } else if (isValueInDatabase(conn, itemStack.getItem(), (short) OreDictionary.WILDCARD_VALUE)) {
                itemData = getItemData(conn, itemStack.getItem(), (short) OreDictionary.WILDCARD_VALUE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemData;
    }

    public void setItemData(ItemStack itemStack, boolean matchMeta, IItemData itemData) {
        short meta = OreDictionary.WILDCARD_VALUE;
        if (matchMeta) {
            meta = (short) itemStack.getMetadata();
        }
        setItemData(itemStack.getItem(), meta, itemData);
    }

    private IItemData getItemData(Connection conn, Item item, short meta) throws SQLException {
        IItemData itemData = ItemData.createEmpty();
        String sql = "SELECT categories, tags, cost FROM " + TABLE_ITEMS_NAME + " WHERE item_reg_name=? AND item_meta=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getRegistryName().toString());
            ps.setShort(2, meta);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    JsonElement categoriesJson = SerializeHelper.stringToJson(resultSet.getString("categories"));
                    JsonElement tagsJson = SerializeHelper.stringToJson(resultSet.getString("tags"));
                    JsonElement costJson = SerializeHelper.stringToJson(resultSet.getString("cost"));

                    ArrayList<String> categories = jsonArrayToArrayList(categoriesJson.getAsJsonArray());
                    ArrayList<String> tags = jsonArrayToArrayList(tagsJson.getAsJsonArray());
                    ICost value = CostSerializer.deserializeJson(costJson);

                    itemData = ItemData.create(categories, tags, value);
                }
            }
        }
        return itemData;
    }

    private void setItemData(Item item, short meta, IItemData itemData) {
        try (Connection conn = getConnection()) {
            if (isValueInDatabase(conn, item, meta)) {
                updateItemData(conn, item, meta, itemData);
            } else {
                setItemData(conn, item, meta, itemData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setItemData(Connection conn, Item item, short meta, IItemData itemData) throws SQLException {
        String sql = "INSERT INTO " + TABLE_ITEMS_NAME + " (id, item_reg_name, item_meta, categories, tags, cost) VALUES (NULL, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getRegistryName().toString());
            ps.setShort(2, meta);
            ps.setString(3, arrayListToJsonArray(itemData.getCategories()).toString());
            ps.setString(4, arrayListToJsonArray(itemData.getTags()).toString());
            ps.setString(5, CostSerializer.serializeJson(itemData.getValue(), false).toString());
            ps.execute();
        }
    }

    private void updateItemData(Connection conn, Item item, short meta, IItemData itemData) throws SQLException {
        String sql = "UPDATE " + TABLE_ITEMS_NAME + " SET categories=? tags=? cost=? WHERE item_reg_name=? AND item_meta=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, arrayListToJsonArray(itemData.getCategories()).toString());
            ps.setString(2, arrayListToJsonArray(itemData.getTags()).toString());
            ps.setString(3, CostSerializer.serializeJson(itemData.getValue(), false).toString());
            ps.setString(4, item.getRegistryName().toString());
            ps.setShort(5, meta);
            ps.execute();
        }
    }

    private boolean isValueInDatabase(Connection conn, Item item, short meta) throws SQLException {
        boolean found = false;
        String sql = "SELECT id FROM " + TABLE_ITEMS_NAME + " WHERE item_reg_name=? AND item_meta=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getRegistryName().toString());
            ps.setShort(2, meta);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    found = true;
                }
            }
        }
        return found;
    }

    private ArrayList<String> jsonArrayToArrayList(JsonArray jsonArray) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            arrayList.add(jsonArray.get(i).getAsString());
        }
        return arrayList;
    }

    private JsonArray arrayListToJsonArray(ArrayList<String> arrayList) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < arrayList.size(); i++) {
            jsonArray.add(arrayList.get(i));
        }
        return jsonArray;
    }
}
