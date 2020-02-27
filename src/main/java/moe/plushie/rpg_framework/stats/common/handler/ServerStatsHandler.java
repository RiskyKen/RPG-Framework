package moe.plushie.rpg_framework.stats.common.handler;

import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.stats.TableStatsServer;
import moe.plushie.rpg_framework.stats.common.StatsTimer;
import moe.plushie.rpg_framework.stats.common.StatsTimer.IStatsResetCallback;
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
                TableStatsServer.create();
            }
        });
    }
    
    @SubscribeEvent
    public void onServerTickEvent(ServerTickEvent event) {
        if (event.phase == Phase.START) {
            TIMER_SERVER.begin();
        }
        if (event.phase != Phase.END) {
            return;
        }
        playersOnline = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().size();
        TIMER_SERVER.end();
    }

    @Override
    public void statsReset(StatsTimer statsTimer) {
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
