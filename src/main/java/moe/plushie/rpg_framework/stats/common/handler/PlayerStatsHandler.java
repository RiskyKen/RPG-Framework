package moe.plushie.rpg_framework.stats.common.handler;

import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.TablePlayers;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.stats.common.database.TableHeatmaps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public final class PlayerStatsHandler {

    public PlayerStatsHandler() {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                TablePlayers.create();
                if (ConfigHandler.optionsLocal.trackHeatmaps) {
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
        if (event.phase != Phase.START & event.side != Side.SERVER) {
            return;
        }
        World world = event.world;
        if (ConfigHandler.optionsLocal.trackHeatmaps) {
            if ((world.getTotalWorldTime() % 20L) != 0) {
                return;
            }
            world.profiler.startSection(LibModInfo.ID);
            world.profiler.startSection("heatmapUpdates");
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
