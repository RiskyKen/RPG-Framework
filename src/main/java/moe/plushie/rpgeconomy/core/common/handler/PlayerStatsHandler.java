package moe.plushie.rpgeconomy.core.common.handler;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.database.TableHeatmaps;
import moe.plushie.rpgeconomy.core.database.TablePlayers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class PlayerStatsHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        TablePlayers.create();
        EntityPlayer player = event.player;
        if (TablePlayers.isPlayerInDatabase(player)) {
            TablePlayers.updatePlayerLastLogin(player);
        } else {
            TablePlayers.addPlayerToDatabase(player);
        }
    }

    @SubscribeEvent
    public static void onClientConnectedToServerEvent(ClientConnectedToServerEvent event) {
        RpgEconomy.getLogger().info("Client connect");
    }

    @SubscribeEvent
    public static void onClientDisconnectionFromServerEvent(ClientDisconnectionFromServerEvent event) {
        RpgEconomy.getLogger().info("Client disconnect");
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerLoggedOutEvent event) {
        RpgEconomy.getLogger().info("Player logout: " + event.player);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.side == Side.CLIENT | event.phase == Phase.START) {
            return;
        }
        if (ConfigHandler.heatmapTrackingRate == 0) {
            return;
        }
        EntityPlayer player = event.player;
        if (player.getEntityWorld() == null) {
            return;
        }
        if ((player.getEntityWorld().getTotalWorldTime() % (20L * ((long) ConfigHandler.heatmapTrackingRate))) != 0) {
            return;
        }
        TableHeatmaps.create();
        TableHeatmaps.addHeatmapData(player);
    }
}
