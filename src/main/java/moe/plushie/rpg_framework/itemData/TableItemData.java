package moe.plushie.rpg_framework.itemData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.serialize.CostSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class TableItemData {

    private final static String TABLE_ITEMS_NAME = "value_items";

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

    private static void createTableItems(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS_NAME;
        sql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        sql += "item_reg_name INTEGER NOT NULL,";
        sql += "item_meta INTEGER NOT NULL,";
        sql += "cost TEXT NOT NULL)";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "CREATE INDEX IF NOT EXISTS idx_item_reg_name ON " + TABLE_ITEMS_NAME + " (item_reg_name)";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public IItemData getItemData(ItemStack itemStack) {
        IItemData itemData = ItemData.createEmpty();
        try (Connection conn = getConnection()) {
            if (isValueInDatabase(conn, itemStack.getItem(), (short) itemStack.getMetadata())) {
                itemData = itemData.setValue(getItemValue(conn, itemStack.getItem(), (short) itemStack.getMetadata()));
            } else if (isValueInDatabase(conn, itemStack.getItem(), (short) OreDictionary.WILDCARD_VALUE)) {
                itemData = itemData.setValue(getItemValue(conn, itemStack.getItem(), (short) OreDictionary.WILDCARD_VALUE));
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
        setItemValue(itemStack.getItem(), meta, itemData.getValue());
    }

    public ICost getItemValue(Connection conn, Item item, short meta) throws SQLException {
        ICost cost = Cost.NO_COST;
        String sql = "SELECT cost FROM " + TABLE_ITEMS_NAME + " WHERE item_reg_name=? AND item_meta=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getRegistryName().toString());
            ps.setShort(2, meta);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    String json = resultSet.getString("cost");
                    cost = CostSerializer.deserializeJson(SerializeHelper.stringToJson(json));
                }
            }
        }
        return cost;
    }

    public void setItemValue(Item item, short meta, ICost cost) {

        try (Connection conn = getConnection()) {
            if (isValueInDatabase(conn, item, meta)) {
                updateItemValue(conn, item, meta, cost);
            } else {
                setItemValue(conn, item, meta, cost);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setItemValue(Connection conn, Item item, short meta, ICost cost) throws SQLException {
        String sql = "INSERT INTO " + TABLE_ITEMS_NAME + " (id, item_reg_name, item_meta, cost) VALUES (NULL, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getRegistryName().toString());
            ps.setShort(2, meta);
            ps.setString(3, CostSerializer.serializeJson(cost, false).toString());
            ps.execute();
        }
    }

    private void updateItemValue(Connection conn, Item item, short meta, ICost cost) throws SQLException {
        String sql = "UPDATE " + TABLE_ITEMS_NAME + " SET cost=? WHERE item_reg_name=? AND item_meta=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, CostSerializer.serializeJson(cost, false).toString());
            ps.setString(2, item.getRegistryName().toString());
            ps.setShort(3, meta);
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
}
