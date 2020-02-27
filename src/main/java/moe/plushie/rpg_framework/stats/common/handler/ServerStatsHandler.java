package moe.plushie.rpg_framework.stats.common.handler;

import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.stats.TableStatsServer;
import moe.plushie.rpg_framework.stats.common.StatsTimer;
import moe.plushie.rpg_framework.stats.common.StatsTimer.IStatsResetCallback;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public final class ServerStatsHandler implements IStatsResetCallback {

    public final StatsTimer TIMER_SERVER = new StatsTimer(20, this);

    private int playersOnline = 0;

    public ServerStatsHandler() {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                if (ConfigHandler.optionsLocal.trackServerStats) {
                    TableStatsServer.create();
                }
            }
        });
    }
    
    private Profiler getProfiler() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().profiler;
    }

    @SubscribeEvent
    public void onServerTickEvent(ServerTickEvent event) {
        getProfiler().startSection("root");
        getProfiler().startSection(LibModInfo.ID);
        getProfiler().startSection("serverStats");
        if (event.phase == Phase.START) {
            TIMER_SERVER.begin();
        }
        if (event.phase == Phase.END) {
            playersOnline = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().size();
            TIMER_SERVER.end();
        }
        getProfiler().endSection();
        getProfiler().endSection();
        getProfiler().endSection();
    }

    @Override
    public void statsReset(StatsTimer statsTimer) {
        if (!ConfigHandler.optionsLocal.trackServerStats) {
            return;
        }
        float average = statsTimer.getAverage();
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                TableStatsServer.addRecords(playersOnline, average, getMemUseMB());
            }
        });
    }

    private int getMemUseMB() {
        Runtime rt = Runtime.getRuntime();
        return (int) (((rt.totalMemory() - rt.freeMemory()) / 1024L) / 1024L);
    }
}
