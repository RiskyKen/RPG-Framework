package moe.plushie.rpgeconomy.core.common.handler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.database.SQLiteDriver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class PlayerStatsHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        // RpgEconomy.getLogger().info("onPlayerLogin");

        SQLiteDriver.executeUpdate(
                "CREATE TABLE IF NOT EXISTS players "
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "uuid VARCHAR(36) NOT NULL,"
                + "username VARCHAR(80) NOT NULL,"
                + "first_seen DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
                + "last_seen DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)");

        String sql = "SELECT * FROM players WHERE uuid='%s'";
        sql = String.format(sql, event.player.getGameProfile().getId().toString());
        ArrayList<String> results = SQLiteDriver.executeQueryArrayList(sql);
        if (results.isEmpty()) {
            addPlayerToDatabase(event.player);
        } else {
            updatePlayerLastLogin(event.player);
        }
    }

    private static void addPlayerToDatabase(EntityPlayer player) {
        String sql = "INSERT INTO players (id, uuid, username, first_seen, last_seen) VALUES (NULL, '%s', '%s', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        sql = String.format(sql, player.getGameProfile().getId().toString(), player.getGameProfile().getName());
        SQLiteDriver.executeUpdate(sql);
    }

    private static void updatePlayerLastLogin(EntityPlayer player) {
        String sql = "UPDATE players SET last_seen=datetime('now') WHERE uuid='%s'";
        Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
        sql = String.format(sql, player.getGameProfile().getId().toString());
        SQLiteDriver.executeUpdate(sql);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.side != Side.SERVER) {
            return;
        }
        if (event.phase == Phase.START) {
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

        int playerId = getPlayerId(player);

        SQLiteDriver.executeUpdate(
                "CREATE TABLE IF NOT EXISTS heatmaps "
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "player_id INTEGER NOT NULL,"   
                + "x DOUBLE NOT NULL,"
                + "y DOUBLE NOT NULL,"
                + "z DOUBLE NOT NULL,"
                + "dimension INTEGER NOT NULL,"
                + "date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)");

        String sql = "INSERT INTO heatmaps (id, player_id, x, y, z, dimension, date) VALUES (NULL, '%d', '%f', '%f', '%f', '%d', CURRENT_TIMESTAMP)";
        sql = String.format(sql, playerId, player.posX, player.posY, player.posZ, player.dimension);

        // RpgEconomy.getLogger().info(sql);

        SQLiteDriver.executeUpdate(sql);
    }

    private static int getPlayerId(EntityPlayer player) {
        String sql = "SELECT id FROM players WHERE uuid='%s'";
        sql = String.format(sql, player.getGameProfile().getId().toString());
        ArrayList<String> results = SQLiteDriver.executeQueryArrayList(sql);
        if (!results.isEmpty()) {
            return Integer.parseInt(results.get(0));
        }
        return -1;
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerLoggedOutEvent event) {
        // RpgEconomy.getLogger().info("onPlayerLogout");
    }
}
