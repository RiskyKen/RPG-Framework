package moe.plushie.rpgeconomy.core.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteJDBCDriverConnection {
	
    public static void connect(File file) {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:" + file.getAbsolutePath();
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
