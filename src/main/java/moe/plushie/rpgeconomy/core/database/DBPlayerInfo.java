package moe.plushie.rpgeconomy.core.database;

import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

public class DBPlayerInfo {

	private int id;
	public UUID uuid;
	private Date firstSeen;
	private Date lastLogin;
	private long playTime;
	
	
	public DBPlayerInfo(ResultSet resultSet) {
		//this.uuid = uuid;
	}
}
