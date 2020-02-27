package moe.plushie.rpg_framework.stats.common.handler;

import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.stats.TableStatsWorld;
import moe.plushie.rpg_framework.stats.common.StatsTimer;
import moe.plushie.rpg_framework.stats.common.StatsTimer.IStatsResetCallback;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public final class WorldStatsHandler implements IStatsResetCallback {

    public WorldStatsHandler() {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                if (ConfigHandler.optionsLocal.trackWorldStats) {
                    TableStatsWorld.create();
                }
            }
        });
    }

    public StatsTimer TIMER_WORLD = new StatsTimer(20, this);

    private int playersCount = 0;
    private int entityCount = 0;
    private int tileCount = 0;
    private int tickingTileCount = 0;

    @SubscribeEvent
    public void onWorldTickEvent(WorldTickEvent event) {
        if (event.side == Side.CLIENT) {
            return;
        }
        if (event.world.provider.getDimension() != 0) {
            return;
        }
        if (event.phase == Phase.START) {
            TIMER_WORLD.begin();
        }
        if (event.phase != Phase.END) {
            return;
        }
        World world = event.world;
        playersCount = world.playerEntities.size();
        entityCount = world.loadedEntityList.size();
        tileCount = world.loadedTileEntityList.size();
        tickingTileCount = world.tickableTileEntities.size();
        TIMER_WORLD.end();
    }

    @Override
    public void statsReset(StatsTimer statsTimer) {
        if (!ConfigHandler.optionsLocal.trackWorldStats) {
            return;
        }
        float tickTime = statsTimer.getAverage();
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                TableStatsWorld.addRecords(0, playersCount, tickTime, entityCount, tileCount, tickingTileCount);
            }
        });
    }
}
