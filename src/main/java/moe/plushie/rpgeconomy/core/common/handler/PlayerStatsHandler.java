package moe.plushie.rpgeconomy.core.common.handler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.database.SQLiteDriver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class PlayerStatsHandler {

	@SubscribeEvent
	public static void onPlayerLogin(PlayerLoggedInEvent event) {
		//RpgEconomy.getLogger().info("onPlayerLogin");
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
	public static void onPlayerLogout(PlayerLoggedOutEvent event) {
		//RpgEconomy.getLogger().info("onPlayerLogout");
	}
}
