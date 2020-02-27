package moe.plushie.rpg_framework.core.common.handler;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.TablePlayers;
import moe.plushie.rpg_framework.core.database.stats.TableHeatmaps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class PlayerStatsHandler {

    static {
        TablePlayers.create();
        if (ConfigHandler.optionsShared.heatmapTrackingRate > 0) {
            TableHeatmaps.create();
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        DatabaseManager.executeAndWait(new Runnable() {

            @Override
            public void run() {
                TablePlayers.updateOrAddPlayer(player.getGameProfile());
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerLoggedOutEvent event) {
        RPGFramework.getLogger().info("Player logout: " + event.player);
    }

    public static final StatsTimer TIMER_SERVER = new StatsTimer(20);
    public static StatsTimer TIMER_WORLD = new StatsTimer(20);

    @SubscribeEvent
    public static void onServerTickEvent(ServerTickEvent event) {
        if (event.phase == Phase.START) {
            TIMER_SERVER.begin();
        }
        if (event.phase != Phase.END) {
            return;
        }
        TIMER_SERVER.end();
    }

    @SubscribeEvent
    public static void onWorldTickEvent(WorldTickEvent event) {
        if (event.side == Side.CLIENT) {
            return;
        }
        if (event.phase == Phase.START) {
            TIMER_WORLD.begin();
        }
        if (event.phase != Phase.END) {
            return;
        }
        TIMER_WORLD.end();

        World world = event.world;
        if (ConfigHandler.optionsShared.heatmapTrackingRate != 0) {
            world.profiler.startSection(LibModInfo.ID + "heatmapUpdates");
            if ((world.getTotalWorldTime() % (20L * (ConfigHandler.optionsShared.heatmapTrackingRate))) != 0) {
                return;
            }
            world.profiler.startSection("createTable");
            world.profiler.endStartSection("updateTable");
            DatabaseManager.createTaskAndExecute(new Runnable() {

                @Override
                public void run() {
                    TableHeatmaps.addHeatmapData(world.playerEntities);
                }
            });
            world.profiler.endSection();
            world.profiler.endSection();
        }

    }

    public static class StatsTimer {

        private int[] history;
        private long lastTick = System.currentTimeMillis();
        private int count;

        public StatsTimer(int historyAmount) {
            history = new int[historyAmount];
        }

        public void begin() {
            lastTick = System.currentTimeMillis();
        }

        public void end() {
            long curTime = System.currentTimeMillis();
            history[count] = (int) (curTime - lastTick);
            lastTick = curTime;
            count++;
            if (count > history.length - 1) {
                count = 0;
            }
        }

        public int getSum() {
            int sum = 0;
            for (int i = 0; i < history.length; i++) {
                sum += history[i];
            }
            return sum;
        }

        public float getAverage() {
            int sum = getSum();
            return (float) sum / (float) history.length;
        }
    }
}
