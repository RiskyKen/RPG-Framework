package moe.plushie.rpg_framework.itemData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonElement;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.serialize.CostSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class TableItemValues {

    private final static String TABLE_NAME = "item_value_overrides";

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
        addColumn(conn, "cost TEXT DEFAULT '{}' NOT NULL");

        sql = "CREATE INDEX IF NOT EXISTS idx_value_item_reg_name ON " + TABLE_NAME + " (item_reg_name, item_meta)";
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

    public void setItemValue(ItemStack itemStack, boolean matchMeta, ICost value) {
        short meta = OreDictionary.WILDCARD_VALUE;
        if (matchMeta) {
            meta = (short) itemStack.getMetadata();
        }
        setItemValue(itemStack.getItem(), meta, value);
    }

    public void setItemValue(Item item, short meta, ICost value) {
        try (Connection conn = getConnection()) {
            if (isValueInDatabase(conn, item, meta)) {
                updateItemValue(conn, item, meta, value);
            } else {
                setItemValue(conn, item, meta, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setItemValue(Connection conn, Item item, short meta, ICost value) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, item_reg_name, item_meta, cost) VALUES (NULL, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getRegistryName().toString());
            ps.setShort(2, meta);
            ps.setString(3, CostSerializer.serializeJson(value, false).toString());
            ps.execute();
        }
    }

    private void updateItemValue(Connection conn, Item item, short meta, ICost value) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET cost=? WHERE item_reg_name=? AND item_meta=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, CostSerializer.serializeJson(value, false).toString());
            ps.setString(2, item.getRegistryName().toString());
            ps.setShort(3, meta);
            ps.execute();
        }
    }

    public ICost getItemValue(ItemStack itemStack) {
        ICost value = Cost.NO_COST;
        try (Connection conn = getConnection()) {
            if (isValueInDatabase(conn, itemStack.getItem(), (short) itemStack.getMetadata())) {
                value = getItemValue(conn, itemStack.getItem(), (short) itemStack.getMetadata());
            } else if (isValueInDatabase(conn, itemStack.getItem(), (short) OreDictionary.WILDCARD_VALUE)) {
                value = getItemValue(conn, itemStack.getItem(), (short) OreDictionary.WILDCARD_VALUE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }

    private ICost getItemValue(Connection conn, Item item, short meta) throws SQLException {
        ICost value = Cost.NO_COST;
        String sql = "SELECT cost FROM " + TABLE_NAME + " WHERE item_reg_name=? AND item_meta=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getRegistryName().toString());
            ps.setShort(2, meta);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    JsonElement costJson = SerializeHelper.stringToJson(resultSet.getString("cost"));
                    value = CostSerializer.deserializeJson(costJson);
                }
            }
        }
        return value;
    }

    public void clearItemValue(ItemStack itemStack, boolean matchMeta) {
        short meta = OreDictionary.WILDCARD_VALUE;
        if (matchMeta) {
            meta = (short) itemStack.getMetadata();
        }
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE item_reg_name=? AND item_meta=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemStack.getItem().getRegistryName().toString());
            ps.setShort(2, meta);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValueInDatabase(Connection conn, Item item, short meta) throws SQLException {
        boolean found = false;
        String sql = "SELECT id FROM " + TABLE_NAME + " WHERE item_reg_name=? AND item_meta=?";
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
