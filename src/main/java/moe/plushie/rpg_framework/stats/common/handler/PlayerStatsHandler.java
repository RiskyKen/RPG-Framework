package moe.plushie.rpg_framework.stats.common.handler;

import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.TablePlayers;
import moe.plushie.rpg_framework.core.database.stats.TableHeatmaps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public final class PlayerStatsHandler {

    public PlayerStatsHandler() {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                TablePlayers.create();
                if (ConfigHandler.optionsShared.heatmapTrackingRate > 0) {
                    TableHeatmaps.create();
                }
            }
        });
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        DatabaseManager.executeAndWait(new Runnable() {

            @Override
            public void run() {
                TablePlayers.updateOrAddPlayer(player.getGameProfile());
            }
        });
    }

    @SubscribeEvent
    public void onWorldTickEvent(WorldTickEvent event) {
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
}
