package moe.plushie.rpg_framework.loot.common;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.loot.ILootTable;
import moe.plushie.rpg_framework.api.loot.ILootTableItem;
import moe.plushie.rpg_framework.api.loot.ILootTablePool;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncLoot;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncLootTables;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncLootTables.SyncType;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpg_framework.core.common.serialize.ItemStackSerialize;
import moe.plushie.rpg_framework.core.database.loot.TableLootPools;
import moe.plushie.rpg_framework.core.database.loot.TableLootTables;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public final class LootTableHelper {

    private LootTableHelper() {
    }

    public static void syncLootTablesToClient(EntityPlayerMP player) {
        ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> categories = new ArrayList<String>();
        TableLootTables.getList(identifiers, names, categories, null);
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncLootTables(SyncType.LOOT_TABLES, identifiers, names, categories), player);
    }

    public static void sendTableToClient(IIdentifier identifier, EntityPlayerMP player) {
        ILootTable table = TableLootTables.get(identifier);
        if (table != null) {
            PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncLoot(table), player);
        }
    }

    public static void syncLootPoolsToClient(EntityPlayerMP player) {
        ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> categories = new ArrayList<String>();
        TableLootPools.getList(identifiers, names, categories, null);
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncLootTables(SyncType.LOOT_POOLS, identifiers, names, categories), player);
    }

    public static void sendPoolToClient(IIdentifier identifier, EntityPlayerMP player) {
        ILootTablePool pool = TableLootPools.get(identifier);
        if (pool != null) {
            PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncLoot(pool), player);
        }
    }
    
    public static JsonElement poolToJson(ILootTablePool pool) {
        return getGson().toJsonTree(pool, LootTablePool.class);
    }
    
    public static ILootTablePool poolFromJson(JsonElement json) {
        return getGson().fromJson(json, LootTablePool.class);
    }
    
    public static ILootTablePool poolFromJson(String json) {
        return getGson().fromJson(json, LootTablePool.class);
    }
    
    public static JsonElement tableToJson(ILootTable table) {
        return getGson().toJsonTree(table, LootTablePool.class);
    }
    
    public static ILootTable tableFromJson(JsonElement json) {
        return getGson().fromJson(json, LootTable.class);
    }
    
    public static ILootTable tableFromJson(String json) {
        return getGson().fromJson(json, LootTable.class);
    }
    
    private static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ItemStack.class, new ItemStackSerialize())
                .registerTypeAdapter(IIdentifier.class, new IdentifierSerialize())
                .registerTypeAdapter(ILootTableItem.class, new LootTableItemSerializer())
                .create();
    }
}
