package moe.plushie.rpg_framework.stats.common.handler;

import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.stats.common.StatsServer;
import moe.plushie.rpg_framework.stats.common.database.TableStatsServer;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public final class ServerStatsHandler {

    private StatsServer statsServer = new StatsServer();

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

    public StatsServer getStatsServer() {
        return statsServer;
    }

    private Profiler getProfiler() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().profiler;
    }

    @SubscribeEvent
    public void onServerTickEvent(ServerTickEvent event) {
        getProfiler().startSection("root");
        getProfiler().startSection(LibModInfo.ID);
        getProfiler().startSection("serverStats");
        statsServer.onServerTickEvent(event);
        getProfiler().endSection();
        getProfiler().endSection();
        getProfiler().endSection();
    }
}
