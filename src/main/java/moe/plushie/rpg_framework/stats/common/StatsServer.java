package moe.plushie.rpg_framework.stats.common;

import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.stats.TableStatsServer;
import moe.plushie.rpg_framework.stats.common.StatsHistory.IStatsResetCallback;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class StatsServer implements IStatsResetCallback {

    private static final int TICKS_IN_SECOND = 20;
    private static final int HISTORY_MULTIPLIER = 16;

    private final StatsHistory historyTickTime;

    private int loadedChunks = 0;
    private int playersOnline = 0;
    private int memUseMB = 0;

    public StatsServer(int longAmountMultiplier) {
        historyTickTime = new StatsHistory(TICKS_IN_SECOND, longAmountMultiplier, this);
    }

    public StatsServer() {
        this(HISTORY_MULTIPLIER);
    }

    public StatsHistory getHistoryTickTime() {
        return historyTickTime;
    }

    public int getLoadedChunks() {
        return loadedChunks;
    }

    public int getPlayersOnline() {
        return playersOnline;
    }

    public int getMemUseMB() {
        return memUseMB;
    }

    public void updateStats(int[] historyTickTime, int loadedChunks, int playersOnline, int memUseMB) {
        this.historyTickTime.add(historyTickTime);
        this.loadedChunks = loadedChunks;
        this.playersOnline = playersOnline;
        this.memUseMB = memUseMB;
    }

    public void onServerTickEvent(ServerTickEvent event) {
        if (event.phase == Phase.START) {
            historyTickTime.begin();
        }
        if (event.phase == Phase.END) {
            int count = 0;
            for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
                count += world.getChunkProvider().getLoadedChunkCount();
            }
            loadedChunks = count;
            playersOnline = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().size();

            Runtime rt = Runtime.getRuntime();
            memUseMB = (int) (((rt.totalMemory() - rt.freeMemory()) / 1024L) / 1024L);

            historyTickTime.end();
        }
    }

    @Override
    public void statsReset(StatsHistory statsTimer) {
        if (!ConfigHandler.optionsLocal.trackServerStats) {
            return;
        }
        float average = statsTimer.getAverageShort();
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                TableStatsServer.addRecords(playersOnline, average, getMemUseMB());
            }
        });
    }
}
