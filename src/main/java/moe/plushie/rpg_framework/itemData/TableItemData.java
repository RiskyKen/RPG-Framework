package moe.plushie.rpg_framework.itemData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder.ISqlBulderCreateTable;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.currency.common.serialize.CostSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class TableItemData {

    private final static String TABLE_ITEMS_NAME = "item_data";

    private final static String COLUMN_ID = "id";
    private final static String COLUMN_REG_NAME = "reg_name";
    private final static String COLUMN_META = "meta";
    private final static String COLUMN_COUNT = "count";
    private final static String COLUMN_CATEGORIES = "categories";
    private final static String COLUMN_COST = "cost";
    private final static String COLUMN_TAGS = "tags";
    private final static String COLUMN_NBT_WHITELIST = "nbt_whitelist";

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
        ISqlBulderCreateTable table = DatabaseManager.getSqlBulder().createTable(TABLE_ITEMS_NAME);
        table.ifNotExists(true);
        table.addColumn(COLUMN_ID, ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true).setAutoIncrement(true);
        table.addColumn(COLUMN_REG_NAME, ISqlBulder.DataType.TEXT).setNotNull(true).setDefault("''");
        table.addColumn(COLUMN_META, ISqlBulder.DataType.INT).setNotNull(true).setDefault("0");
        table.addColumn(COLUMN_COUNT, ISqlBulder.DataType.INT).setNotNull(true).setDefault("0");
        table.addColumn(COLUMN_CATEGORIES, ISqlBulder.DataType.TEXT).setNotNull(true).setDefault("'[]'");
        table.addColumn(COLUMN_COST, ISqlBulder.DataType.TEXT).setNotNull(true).setDefault("'{}'");
        table.addColumn(COLUMN_TAGS, ISqlBulder.DataType.TEXT).setNotNull(true).setDefault("'[]'");
        table.addColumn(COLUMN_NBT_WHITELIST, ISqlBulder.DataType.TEXT).setNotNull(true).setDefault("''");
        table.addKey("idx_item_reg", false, COLUMN_REG_NAME, COLUMN_META, COLUMN_COUNT, COLUMN_NBT_WHITELIST);

        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(table.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setItemData(IItemMatcher itemMatcher, IItemData itemData) {
        Item item = itemMatcher.getItemStack().getItem();
        short meta = OreDictionary.WILDCARD_VALUE;
        short count = 0;
        String nbtWhiteList = "";

        if (itemMatcher.isMatchMeta()) {
            meta = (short) itemMatcher.getItemStack().getMetadata();
        }
        if (itemMatcher.isMatchCount()) {
            count = (short) itemMatcher.getItemStack().getCount();
        }
        if (itemMatcher.getItemStack().hasTagCompound()) {
            nbtWhiteList = itemMatcher.getItemStack().getTagCompound().toString();
        }

        try (Connection conn = getConnection()) {
            if (isValueInDatabase(conn, item, meta, count, nbtWhiteList)) {
                updateItemData(conn, item, meta, count, nbtWhiteList, itemData);
            } else {
                setItemData(conn, item, meta, count, nbtWhiteList, itemData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setItemData(Connection conn, Item item, short meta, short count, String nbtWhiteList, IItemData itemData) throws SQLException {
        String sql = "INSERT INTO " + TABLE_ITEMS_NAME + " (id, reg_name, meta, count, nbt_whitelist, categories, tags, cost) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getRegistryName().toString());
            ps.setShort(2, meta);
            ps.setShort(3, count);
            ps.setString(4, nbtWhiteList);
            ps.setString(5, arrayListToJsonArray(itemData.getCategories()).toString());
            ps.setString(6, arrayListToJsonArray(itemData.getTags()).toString());
            ps.setString(7, CostSerializer.serializeJson(itemData.getValue(), false).toString());
            ps.execute();
        }
    }

    private void updateItemData(Connection conn, Item item, short meta, short count, String nbtWhiteList, IItemData itemData) throws SQLException {
        String sql = "UPDATE " + TABLE_ITEMS_NAME + " SET categories=?, tags=?, cost=? WHERE reg_name=? AND meta=? AND count=? AND nbt_whitelist=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, arrayListToJsonArray(itemData.getCategories()).toString());
            ps.setString(2, arrayListToJsonArray(itemData.getTags()).toString());
            ps.setString(3, CostSerializer.serializeJson(itemData.getValue(), false).toString());
            ps.setString(4, item.getRegistryName().toString());
            ps.setShort(5, meta);
            ps.setShort(6, count);
            ps.setString(7, nbtWhiteList);
            ps.execute();
        }
    }

    public IItemData getItemData(ItemStack itemStack) {
        IItemData itemData = ItemData.createEmpty();
        Item item = itemStack.getItem();
        short meta = (short) itemStack.getMetadata();
        short count = 0;
        String nbt = "";

//        if (itemMatcher.isMatchMeta()) {
//            meta = (short) itemStack.getMetadata();
//        }
//        if (itemMatcher.isMatchCount()) {
//            count = (short) itemStack.getCount();
//        }
//        if (itemMatcher.getItemStack().hasTagCompound()) {
//            nbt = itemMatcher.getItemStack().getTagCompound().toString();
//        }

        try (Connection conn = getConnection()) {
            if (isValueInDatabase(conn, item, meta, count, nbt)) {
                itemData = getItemData(conn, item, meta, count, nbt);
            } else if (isValueInDatabase(conn, item, (short) OreDictionary.WILDCARD_VALUE, count, nbt)) {
                itemData = getItemData(conn, item, (short) OreDictionary.WILDCARD_VALUE, count, nbt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemData;
    }

    private IItemData getItemData(Connection conn, Item item, short meta, short count, String nbtWhiteList) throws SQLException {
        IItemData itemData = ItemData.createEmpty();
        String sql = "SELECT categories, cost, tags FROM " + TABLE_ITEMS_NAME + " WHERE reg_name=? AND meta=? AND count=? AND nbt_whitelist=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getRegistryName().toString());
            ps.setShort(2, meta);
            ps.setShort(3, count);
            ps.setString(4, nbtWhiteList);
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

    private boolean isValueInDatabase(Connection conn, Item item, short meta, short count, String nbtWhiteList) throws SQLException {
        boolean found = false;
        String sql = "SELECT id FROM " + TABLE_ITEMS_NAME + " WHERE reg_name=? AND meta=? AND count=? AND nbt_whitelist=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getRegistryName().toString());
            ps.setShort(2, meta);
            ps.setShort(3, count);
            ps.setString(4, nbtWhiteList);
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
