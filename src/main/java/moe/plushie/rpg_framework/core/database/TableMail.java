package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.mail.common.MailListItem;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public final class TableMail {

    private TableMail() {
    }

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS mail"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "mail_system VARCHAR(64) NOT NULL,"
            + "player_id_sender INTEGER NOT NULL,"
            + "player_id_receiver INTEGER NOT NULL,"
            + "subject VARCHAR(64) NOT NULL,"
            + "text TEXT NOT NULL,"
            + "attachments TEXT NOT NULL,"
            + "sent_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
            + "read BOOLEAN NOT NULL)";

    public static void create() {
        SQLiteDriver.executeUpdate(SQL_CREATE_TABLE);
    }

    private static final String SQL_MESSAGE_ADD = "INSERT INTO mail (id, mail_system, player_id_sender, player_id_receiver, subject, text, attachments, sent_date, read) VALUES (NULL, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

    public static void addMessage(MailMessage message) {
        try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_ADD)) {
            DBPlayer dbPlayerSender = TablePlayers.getPlayer(message.getSender());
            DBPlayer dbPlayerReceiver = TablePlayers.getPlayer(message.getReceiver());
            ps.setObject(1, message.getMailSystem().getIdentifier().getValue());
            ps.setInt(2, dbPlayerSender.getId());
            ps.setInt(3, dbPlayerReceiver.getId());
            ps.setString(4, message.getSubject());
            ps.setString(5, message.getMessageText());
            ps.setString(6, "[]");
            ps.setBoolean(7, false);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String SQL_MESSAGE_LIST_GET = "SELECT id, subject, attachments, read FROM mail WHERE mail_system=?";

    public static ArrayList<MailListItem> getMessageList(EntityPlayer player, IMailSystem mailSystem) {
        ArrayList<MailListItem> listItems = new ArrayList<MailListItem>();
        try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_LIST_GET)) {
            ps.setObject(1, mailSystem.getIdentifier().getValue());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                listItems.add(new MailListItem(resultSet.getInt("id"), resultSet.getString("subject"), resultSet.getString("attachments").length() > 2, resultSet.getBoolean("read")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listItems;
    }

    private static final String SQL_MESSAGE_GET = "SELECT * FROM mail WHERE id=?";

    public static MailMessage getMessage(int id) {
        MailMessage message = null;
        try (Connection conn = SQLiteDriver.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_GET)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                IMailSystem mailSystem = RpgEconomy.getProxy().getMailSystemManager().getMailSystem(new IdentifierString(resultSet.getString("mail_system")));
                DBPlayerInfo dbPlayerSender = TablePlayers.getPlayer(conn, resultSet.getInt("sender"));
                DBPlayerInfo dbPlayerReceiver = TablePlayers.getPlayer(conn, resultSet.getInt("receiver"));
                Date sendDateTime = resultSet.getDate("sent_date");
                String subject = resultSet.getString("subject");
                String messageText = resultSet.getString("text");
                NonNullList<ItemStack> attachments = NonNullList.<ItemStack>create();

                message = new MailMessage(mailSystem, dbPlayerSender.getGameProfile(), dbPlayerReceiver.getGameProfile(), sendDateTime, subject, messageText, attachments);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }
}
