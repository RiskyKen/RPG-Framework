package moe.plushie.rpgeconomy.loot.common;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncLootTable;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncLootTable.SyncType;
import moe.plushie.rpgeconomy.core.database.TableLootPools;
import moe.plushie.rpgeconomy.core.database.TableLootTables;
import net.minecraft.entity.player.EntityPlayerMP;

public final class LootTableHelper {

    private LootTableHelper() {
    }

    public static void syncLootTablesToClient(EntityPlayerMP player) {
        ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> categories = new ArrayList<String>();
        TableLootTables.getList(identifiers, names, categories, null);
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncLootTable(SyncType.LOOT_TABLES, identifiers, names, categories), player);
    }

    public static void syncLootPoolsToClient(EntityPlayerMP player) {
        ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> categories = new ArrayList<String>();
        TableLootPools.getList(identifiers, names, categories, null);
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncLootTable(SyncType.LOOT_POOLS, identifiers, names, categories), player);
    }
}
