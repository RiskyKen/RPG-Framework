package moe.plushie.rpg_framework.stats.common.inventory;

import java.util.HashMap;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.inventory.ModContainer;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerStatsUpdate;
import moe.plushie.rpg_framework.stats.ModuleStats;
import moe.plushie.rpg_framework.stats.common.StatsServer;
import moe.plushie.rpg_framework.stats.common.StatsWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class ContainerStats extends ModContainer {

    private Object statsLock = new Object();
    private StatsServer statsServer = new StatsServer();
    private HashMap<Integer, StatsWorld> worldStatsMap = new HashMap<Integer, StatsWorld>();
    private int counter;

    public ContainerStats(EntityPlayer entityPlayer) {
        super(entityPlayer.inventory);
        sendStatsToClient(true);
    }

    private void sendStatsToClient(boolean fullStats) {
        World world = invPlayer.player.getEntityWorld();
        if (world.isRemote) {
            return;
        }
        if (!RPGFramework.isDedicated()) {
            return;
        }
        MessageServerStatsUpdate message = new MessageServerStatsUpdate(fullStats, ModuleStats.getServerStatsHandler().getStatsServer(), ModuleStats.getWorldStatsHandler().getWorldStats(world.provider.getDimension()));
        PacketHandler.NETWORK_WRAPPER.sendTo(message, (EntityPlayerMP) invPlayer.player);
    }

    public StatsWorld getWorldStats(int dimensionID) {
        return worldStatsMap.get(Integer.valueOf(dimensionID));
    }

    public void setServerStats(StatsServer update) {
        if (this.statsServer == null) {
            this.statsServer = new StatsServer();
        }
        statsServer.updateStats(update.getHistoryTickTime().getHistory(), update.getLoadedChunks(), update.getPlayersOnline(), update.getMemUseMB());
    }

    public void setWorldStats(StatsWorld update) {
        Integer id = Integer.valueOf(update.getDimensionID());
        if (!worldStatsMap.containsKey(id)) {
            worldStatsMap.put(id, new StatsWorld(id.intValue()));
        }
        StatsWorld statsWorld = worldStatsMap.get(id);
        statsWorld.updateStats(update.getHistoryTickTime().getHistory(), update.getPlayersCount(), update.getEntityCount(), update.getTileCount(), update.getTickingTileCount());
    }

    public void gotServerUpdate(StatsServer statsServer, StatsWorld... statsWorlds) {
        synchronized (statsLock) {
            setServerStats(statsServer);
            for (StatsWorld statsWorld : statsWorlds) {
                setWorldStats(statsWorld);
            }
        }
    }

    public StatsServer getStatsServer() {
        synchronized (statsLock) {
            return statsServer;
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        counter++;
        if (counter >= 20) {
            counter = 0;
            sendStatsToClient(false);
        }
    }
}
