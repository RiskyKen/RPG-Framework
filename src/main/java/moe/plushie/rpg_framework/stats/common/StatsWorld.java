package moe.plushie.rpg_framework.stats.common;

import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.stats.TableStatsWorld;
import moe.plushie.rpg_framework.stats.common.StatsHistory.IStatsResetCallback;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class StatsWorld implements IStatsResetCallback {

    private static final int TICKS_IN_SECOND = 20;
    private static final int HISTORY_MULTIPLIER = 16;

    private final int dimensionID;

    private final StatsHistory historyTickTime;
    // private StatsHistory historyPlayerCount = new StatsHistory(TICKS_IN_SECOND, HISTORY_MULTIPLIER);
    // private StatsHistory historyEntityCount = new StatsHistory(TICKS_IN_SECOND, HISTORY_MULTIPLIER);
    // private StatsHistory historyTileCount = new StatsHistory(TICKS_IN_SECOND, HISTORY_MULTIPLIER);
    // private StatsHistory historyTickingTileCount = new StatsHistory(TICKS_IN_SECOND, HISTORY_MULTIPLIER);

    private int playersCount = 0;
    private int entityCount = 0;
    private int tileCount = 0;
    private int tickingTileCount = 0;

    public StatsWorld(int dimensionID) {
        this(dimensionID, HISTORY_MULTIPLIER);
    }

    public StatsWorld(int dimensionID, int longAmountMultiplier) {
        this.dimensionID = dimensionID;
        historyTickTime = new StatsHistory(TICKS_IN_SECOND, longAmountMultiplier, this);
    }

    public int getDimensionID() {
        return dimensionID;
    }

    public StatsHistory getHistoryTickTime() {
        return historyTickTime;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    // public StatsHistory getHistoryPlayerCount() {
    // return historyPlayerCount;
    // }

    public int getEntityCount() {
        return entityCount;
    }

    // public StatsHistory getHistoryEntityCount() {
    // return historyEntityCount;
    // }

    public int getTileCount() {
        return tileCount;
    }

    // public StatsHistory getHistoryTileCount() {
    // return historyTileCount;
    // }

    public int getTickingTileCount() {
        return tickingTileCount;
    }

    // public StatsHistory getHistoryTickingTileCount() {
    // return historyTickingTileCount;
    // }

    public void updateStats(int[] historyTickTime, int playersCount, int entityCount, int tileCount, int tickingTileCount) {
        this.historyTickTime.add(historyTickTime);
        this.playersCount = playersCount;
        this.entityCount = entityCount;
        this.tileCount = tileCount;
        this.tickingTileCount = tickingTileCount;
    }
    
    public void onWorldTickEvent(WorldTickEvent event) {
        World world = event.world;
        world.profiler.startSection(LibModInfo.ID);
        world.profiler.startSection("worldStats");
        if (event.phase == Phase.START) {
            historyTickTime.begin();
        }
        if (event.phase == Phase.END) {
            playersCount = world.playerEntities.size();
            entityCount = world.loadedEntityList.size();
            tileCount = world.loadedTileEntityList.size();
            tickingTileCount = world.tickableTileEntities.size();

            // historyPlayerCount.add(playersCount);
            // historyEntityCount.add(entityCount);
            // historyTileCount.add(tileCount);
            // historyTickingTileCount.add(tickingTileCount);

            historyTickTime.end();
        }
        world.profiler.endSection();
        world.profiler.endSection();
    }

    @Override
    public void statsReset(StatsHistory statsTimer) {
        if (!ConfigHandler.optionsLocal.trackWorldStats) {
            return;
        }
        float tickTime = statsTimer.getAverageShort();
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                TableStatsWorld.addRecords(0, playersCount, tickTime, entityCount, tileCount, tickingTileCount);
            }
        });
    }
}
