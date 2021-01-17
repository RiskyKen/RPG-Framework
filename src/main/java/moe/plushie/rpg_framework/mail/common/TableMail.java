package moe.plushie.rpg_framework.mail.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.JsonArray;

import moe.plushie.rpg_framework.api.mail.IMailMessage;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.database.DBPlayer;
import moe.plushie.rpg_framework.core.common.database.DBPlayerInfo;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.DatebaseTable;
import moe.plushie.rpg_framework.core.common.database.TablePlayers;
import moe.plushie.rpg_framework.core.common.database.driver.MySqlBuilder;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder;
import moe.plushie.rpg_framework.core.common.database.sql.ISqlBulder.ISqlBulderCreateTable;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public final class TableMail {

    private final static String TABLE_NAME = "mail";

    private TableMail() {
    }

    private static DatebaseTable getDatebaseTable() {
        return DatebaseTable.PLAYER_DATA;
    }

    private static Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(getDatebaseTable());
    }

//    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS mail"
//            + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
//            + "mail_system VARCHAR(64) NOT NULL,"
//            + "player_id_sender INTEGER NOT NULL,"
//            + "player_id_receiver INTEGER NOT NULL,"
//            + "subject VARCHAR(64) NOT NULL,"
//            + "text TEXT NOT NULL,"
//            + "attachments TEXT NOT NULL,"
//            + "sent_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
//            + "read BOOLEAN NOT NULL)";

    public static void create() {
        try (Connection connection = getConnection()) {
            create(connection, DatabaseManager.getSqlBulder());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void create(Connection connection, ISqlBulder sqlBulder) {
        ISqlBulderCreateTable table = sqlBulder.createTable(TABLE_NAME);
        table.addColumn("id", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true).setAutoIncrement(true);
        table.addColumn("mail_system", ISqlBulder.DataType.VARCHAR).setSize(64).setNotNull(true);
        table.addColumn("player_id_sender", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true);
        table.addColumn("player_id_receiver", ISqlBulder.DataType.INT).setUnsigned(true).setNotNull(true);
        table.addColumn("subject", ISqlBulder.DataType.VARCHAR).setSize(64).setNotNull(true);
        table.addColumn("text", ISqlBulder.DataType.TEXT).setNotNull(true);
        table.addColumn("attachments", ISqlBulder.DataType.TEXT).setNotNull(true);
        table.addColumn("sent_date", ISqlBulder.DataType.DATETIME).setNotNull(true).setDefault("CURRENT_TIMESTAMP");
        table.addColumn("read", ISqlBulder.DataType.BOOLEAN).setNotNull(true);
        table.ifNotExists(true);
        table.setPrimaryKey("id");
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(table.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
//            statement.executeUpdate(SQL_CREATE_TABLE);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    private static final String SQL_MESSAGE_ADD = "INSERT INTO mail (`id`, `mail_system`, `player_id_sender`, `player_id_receiver`, `subject`, `text`, `attachments`, `sent_date`, `read`) VALUES (NULL, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

    public static boolean addMessage(IMailMessage message) {
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_ADD)) {
            DBPlayer dbPlayerSender = TablePlayers.getPlayer(message.getSender());
            DBPlayer dbPlayerReceiver = TablePlayers.getPlayer(message.getReceiver());
            if (dbPlayerReceiver != DBPlayer.MISSING) {
                ps.setObject(1, message.getMailSystem().getIdentifier().getValue());
                ps.setInt(2, dbPlayerSender.getId());
                ps.setInt(3, dbPlayerReceiver.getId());
                ps.setString(4, message.getSubject());
                ps.setString(5, message.getMessageText());
                ps.setString(6, SerializeHelper.writeItemsToJson(message.getAttachments(), false).toString());
                ps.setBoolean(7, false);
                ps.executeUpdate();
                conn.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static final String SQL_MESSAGE_LIST_GET = "SELECT id, subject, attachments, read FROM mail WHERE mail_system=?";

    public static ArrayList<MailListItem> getMessageList(EntityPlayer player, IMailSystem mailSystem) {
        ArrayList<MailListItem> listItems = new ArrayList<MailListItem>();
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_LIST_GET)) {
            ps.setObject(1, mailSystem.getIdentifier().getValue());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                listItems.add(new MailListItem(resultSet.getInt("id"), resultSet.getString("subject"), resultSet.getString("attachments").length() > 2, resultSet.getBoolean("read")));
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listItems;
    }

    private static final String SQL_MESSAGES_GET = "SELECT * FROM mail WHERE mail_system=? AND player_id_receiver=?";

    public static ArrayList<MailMessage> getMessages(DBPlayer player, IMailSystem mailSystem) {
        DBPlayerInfo dbPlayer = TablePlayers.getPlayer(player.getId());
        return getMessages(dbPlayer, mailSystem);
    }

    public static ArrayList<MailMessage> getMessages(EntityPlayer player, IMailSystem mailSystem) {
        DBPlayerInfo dbPlayer = TablePlayers.getPlayerInfo(player.getGameProfile());
        return getMessages(dbPlayer, mailSystem);
    }

    public static ArrayList<MailMessage> getMessages(DBPlayerInfo player, IMailSystem mailSystem) {
        ArrayList<MailMessage> mailMessages = new ArrayList<MailMessage>();
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGES_GET)) {
            ps.setObject(1, mailSystem.getIdentifier().getValue());
            ps.setInt(2, player.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                DBPlayerInfo dbPlayerSender = TablePlayers.getPlayer(resultSet.getInt("player_id_sender"));
                Date sendDateTime = resultSet.getDate("sent_date");
                String subject = resultSet.getString("subject");
                String messageText = resultSet.getString("text");
                JsonArray jsonArray = SerializeHelper.stringToJson(resultSet.getString("attachments")).getAsJsonArray();
                NonNullList<ItemStack> attachments = SerializeHelper.readItemsFromJson(jsonArray);
                boolean read = resultSet.getBoolean("read");
                mailMessages.add(new MailMessage(id, mailSystem, dbPlayerSender.getGameProfile(), player.getGameProfile(), sendDateTime, subject, messageText, attachments, read));
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mailMessages;
    }

    private static final String SQL_UNREAD_MESSAGES_GET = "SELECT COUNT(*) FROM mail WHERE `mail_system`=? AND `player_id_receiver`=? AND `read`=?";

    public static int getUnreadMessagesCount(EntityPlayer entityPlayer, IMailSystem mailSystem) {
        int count = 0;
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(SQL_UNREAD_MESSAGES_GET)) {
            DBPlayerInfo dbPlayerReceiver = TablePlayers.getPlayerInfo(entityPlayer.getGameProfile());
            ps.setObject(1, mailSystem.getIdentifier().getValue());
            ps.setInt(2, dbPlayerReceiver.getId());
            ps.setBoolean(3, false);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static final String SQL_MESSAGE_GET = "SELECT * FROM mail WHERE id=?";

    public static MailMessage getMessage(int id) {

        MailMessage message = null;
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_GET)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                IMailSystem mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(new IdentifierString(resultSet.getString("mail_system")));
                DBPlayerInfo dbPlayerSender = TablePlayers.getPlayer(conn, resultSet.getInt("player_id_sender"));
                DBPlayerInfo dbPlayerReceiver = TablePlayers.getPlayer(conn, resultSet.getInt("player_id_receiver"));
                Date sendDateTime = resultSet.getDate("sent_date");
                String subject = resultSet.getString("subject");
                String messageText = resultSet.getString("text");
                JsonArray jsonArray = SerializeHelper.stringToJson(resultSet.getString("attachments")).getAsJsonArray();
                NonNullList<ItemStack> attachments = SerializeHelper.readItemsFromJson(jsonArray);
                boolean read = resultSet.getBoolean("read");
                message = new MailMessage(id, mailSystem, dbPlayerSender.getGameProfile(), dbPlayerReceiver.getGameProfile(), sendDateTime, subject, messageText, attachments, read);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }

    private static final String SQL_DELETE_MESSAGE = "DELETE FROM mail WHERE id=?";

    public static void deleteMessage(int messageId) {
        create();
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(SQL_DELETE_MESSAGE)) {
            ps.setInt(1, messageId);
            ps.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String SQL_MESSAGE_MARK_READ = "UPDATE mail SET `read`=? WHERE `id`=?";

    public static void markMessageasRead(int messageId) {
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_MARK_READ)) {
            ps.setBoolean(1, true);
            ps.setInt(2, messageId);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String SQL_CLEAR_MESSAGE_ITEMS = "UPDATE mail SET attachments=? WHERE id=?";

    public static void clearMessageItems(int messageId) {
        try (Connection conn = DatabaseManager.getConnection(DatebaseTable.PLAYER_DATA); PreparedStatement ps = conn.prepareStatement(SQL_CLEAR_MESSAGE_ITEMS)) {
            ps.setString(1, "[]");
            ps.setInt(2, messageId);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void importData(ArrayList<MailMessage> mailMessages, Connection connection, boolean dropTable) {
        if (dropTable) {
            String sqlDrop = "DROP TABLE IF EXISTS mail";
            try (PreparedStatement ps = connection.prepareStatement(sqlDrop)) {
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            create(connection, new MySqlBuilder());
        }
        
        DBPlayer dbPlayerSender;
        DBPlayer dbPlayerReceiver;
        
        String sql = "INSERT INTO mail (`id`, `mail_system`, `player_id_sender`, `player_id_receiver`, `subject`, `text`, `attachments`, `sent_date`, `read`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (MailMessage mailMessage : mailMessages) {
                ps.addBatch("ALTER TABLE mail AUTO_INCREMENT=" + mailMessage.getId() + ";");
                try {
                    dbPlayerSender = TablePlayers.getPlayer(connection, mailMessage.getSender());
                    dbPlayerReceiver = TablePlayers.getPlayer(connection, mailMessage.getReceiver());
                } catch (SQLException e) {
                    e.printStackTrace();
                    continue;
                }
                ps.setObject(1, mailMessage.getMailSystem().getIdentifier().getValue());
                ps.setInt(2, dbPlayerSender.getId());
                ps.setInt(3, dbPlayerReceiver.getId());
                ps.setString(4, mailMessage.getSubject());
                ps.setString(5, mailMessage.getMessageText());
                ps.setString(6, SerializeHelper.writeItemsToJson(mailMessage.getAttachments(), false).toString());
                ps.setObject(7, mailMessage.getSendDateTime());
                ps.setBoolean(8, mailMessage.isRead());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        


//        for (MailMessage mailMessage : mailMessages) {
//            try (PreparedStatement ps = connection.prepareStatement("ALTER TABLE mail AUTO_INCREMENT=" + mailMessage.getId())) {
//                ps.execute();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            DBPlayer dbPlayerSender;
//            DBPlayer dbPlayerReceiver;
//            RPGFramework.getLogger().info(mailMessage);
//            try {
//                dbPlayerSender = TablePlayers.getPlayer(connection, mailMessage.getSender());
//                dbPlayerReceiver = TablePlayers.getPlayer(connection, mailMessage.getReceiver());
//                String sql = "INSERT INTO mail (`id`, `mail_system`, `player_id_sender`, `player_id_receiver`, `subject`, `text`, `attachments`, `sent_date`, `read`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)";
//                try (PreparedStatement ps = connection.prepareStatement(sql)) {
//                    ps.setObject(1, mailMessage.getMailSystem().getIdentifier().getValue());
//                    ps.setInt(2, dbPlayerSender.getId());
//                    ps.setInt(3, dbPlayerReceiver.getId());
//                    ps.setString(4, mailMessage.getSubject());
//                    ps.setString(5, mailMessage.getMessageText());
//                    ps.setString(6, SerializeHelper.writeItemsToJson(mailMessage.getAttachments(), false).toString());
//                    ps.setObject(7, mailMessage.getSendDateTime());
//                    ps.setBoolean(8, mailMessage.isRead());
//                    ps.execute();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public static ArrayList<MailMessage> exportData(Connection connection) {
        ArrayList<MailMessage> mailMessages = new ArrayList<MailMessage>();
        String sql = "SELECT * FROM mail";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    IMailSystem mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(new IdentifierString(resultSet.getString("mail_system")));
                    DBPlayerInfo dbPlayerSender = TablePlayers.getPlayer(connection, resultSet.getInt("player_id_sender"));
                    DBPlayerInfo dbPlayerReceiver = TablePlayers.getPlayer(connection, resultSet.getInt("player_id_receiver"));
                    Date sendDateTime = resultSet.getDate("sent_date");
                    String subject = resultSet.getString("subject");
                    String messageText = resultSet.getString("text");
                    JsonArray jsonArray = SerializeHelper.stringToJson(resultSet.getString("attachments")).getAsJsonArray();
                    NonNullList<ItemStack> attachments = SerializeHelper.readItemsFromJson(jsonArray);
                    boolean read = resultSet.getBoolean("read");
                    if (dbPlayerReceiver.isMissing() | dbPlayerSender.isMissing()) {
                        continue;
                    }
                    if (mailSystem == null) {
                        continue;
                    }
                    mailMessages.add(new MailMessage(id, mailSystem, dbPlayerSender.getGameProfile(), dbPlayerReceiver.getGameProfile(), sendDateTime, subject, messageText, attachments, read));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mailMessages;
    }
}
