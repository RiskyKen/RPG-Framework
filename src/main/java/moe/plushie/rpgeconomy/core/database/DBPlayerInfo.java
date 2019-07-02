package moe.plushie.rpgeconomy.core.database;

import java.util.Date;

import com.mojang.authlib.GameProfile;

public class DBPlayerInfo extends DBPlayer {

	private final GameProfile gameProfile;
	private final Date firstSeen;
	private final Date lastLogin;
	
    public DBPlayerInfo(int id, GameProfile gameProfile, Date firstSeen, Date lastLogin) {
        super(id);
        this.gameProfile = gameProfile;
        this.firstSeen = firstSeen;
        this.lastLogin = lastLogin;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    public Date getFirstSeen() {
        return firstSeen;
    }

    public Date getLastLogin() {
        return lastLogin;
    }
}
