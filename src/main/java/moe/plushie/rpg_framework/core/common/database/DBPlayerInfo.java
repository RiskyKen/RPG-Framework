package moe.plushie.rpg_framework.core.common.database;

import java.util.Date;

import com.mojang.authlib.GameProfile;

public class DBPlayerInfo extends DBPlayer {

    public static final DBPlayerInfo MISSING_INFO = new DBPlayerInfo(-1, null, null, null);
    
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
    
    @Override
    public boolean isMissing() {
        if (super.isMissing()) {
            return true;
        }
        if (this == MISSING_INFO) {
            return true;
        }
        return false;
    }
}
