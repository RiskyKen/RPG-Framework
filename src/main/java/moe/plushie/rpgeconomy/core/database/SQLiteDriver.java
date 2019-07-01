package moe.plushie.rpgeconomy.core.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import moe.plushie.rpgeconomy.core.RpgEconomy;

public final class SQLiteDriver {

	private static final String FILE_NAME = "rpg.sqlite3";

	public static Connection getConnection() throws SQLException {
		File file = new File(RpgEconomy.getProxy().getModDirectory(), FILE_NAME);
		String url = "jdbc:sqlite:" + file.getAbsolutePath();
		return DriverManager.getConnection(url);
	}
	
	public static PreparedStatement getPreparedStatement(String sql) {
	    try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
	        return ps;
	    } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	public static void executeUpdate(String sql) {
		try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
			statement.setQueryTimeout(10);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void executeUpdate(String... sql) {
		try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
			statement.setQueryTimeout(10);
			for (String s : sql) {
				statement.executeUpdate(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> executeQueryArrayList(String sql) {
		ArrayList<String> results = new ArrayList<String>();
		try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
			statement.setQueryTimeout(10);
			try (ResultSet rs = statement.executeQuery(sql)) {
				while (rs.next()) {
					String line = rs.getString(1);
					for (int i = 2; i < rs.getMetaData().getColumnCount() + 1; i++) {
						line += " - "+ rs.getString(i);
					}
					results.add(line);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
}
