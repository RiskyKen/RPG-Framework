package moe.plushie.rpg_framework.stats.common.handler;

import java.util.HashMap;

import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.stats.TableStatsWorld;
import moe.plushie.rpg_framework.stats.common.StatsWorld;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public final class WorldStatsHandler {

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

    private Profiler getProfiler() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().profiler;
    }

    private HashMap<Integer, StatsWorld> worldStatsMap = new HashMap<Integer, StatsWorld>();
    
    public StatsWorld getWorldStats(int dimensionID) {
        return worldStatsMap.get(Integer.valueOf(dimensionID));
    }
    
    @SubscribeEvent
    public void onWorldTickEvent(WorldTickEvent event) {
        if (event.side == Side.CLIENT) {
            return;
        }
        Integer id = Integer.valueOf(event.world.provider.getDimension());
        if (!worldStatsMap.containsKey(id)) {
            worldStatsMap.put(id, new StatsWorld(id.intValue()));
        }
        worldStatsMap.get(id).onWorldTickEvent(event);
    }
}
