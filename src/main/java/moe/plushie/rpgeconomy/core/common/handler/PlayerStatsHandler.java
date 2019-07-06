package moe.plushie.rpgeconomy.core.common.handler;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.database.TableHeatmaps;
import moe.plushie.rpgeconomy.core.database.TablePlayers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class PlayerStatsHandler {
    
    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        TablePlayers.create();
        EntityPlayer player = event.player;
        if (TablePlayers.isPlayerInDatabase(player.getGameProfile())) {
            TablePlayers.updatePlayerLastLogin(player.getGameProfile());
        } else {
            TablePlayers.addPlayerToDatabase(player.getGameProfile());
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerLoggedOutEvent event) {
        RpgEconomy.getLogger().info("Player logout: " + event.player);
    }
    
    @SubscribeEvent
    public static void onWorldTickEvent(WorldTickEvent event) {
        if (event.phase == Phase.START | event.side == Side.CLIENT | ConfigHandler.options.heatmapTrackingRate == 0) {
            return;
        }
        World world = event.world;
        world.profiler.startSection(LibModInfo.ID + ":heatmapUpdates");
        if ((world.getTotalWorldTime() % (20L * ((long) ConfigHandler.options.heatmapTrackingRate))) != 0) {
            return;
        }
        world.profiler.startSection("createTable");
        TableHeatmaps.create();
        world.profiler.endStartSection("updateTable");
        TableHeatmaps.addHeatmapData(world.playerEntities);
        world.profiler.endSection();
        world.profiler.endSection();
    }
}
