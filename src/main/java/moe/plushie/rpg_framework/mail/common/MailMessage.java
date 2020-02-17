package moe.plushie.rpg_framework.mail.common;

import java.util.Date;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.mail.IMailMessage;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class MailMessage implements IMailMessage {

    public static final int SUBJECT_CHARACTER_LIMIT = 32;
    
    private final int id;
    private final IMailSystem mailSystem;
    private final GameProfile sender;
    private final GameProfile receiver;
    private final Date sendDateTime;
    private final String subject;
    private final String messageText;
    private final NonNullList<ItemStack> attachments;
    private boolean read;

    public MailMessage(int id, IMailSystem mailSystem, GameProfile sender, GameProfile receiver, Date sendDateTime, String subject, String messageText, NonNullList<ItemStack> attachments, boolean read) {
        this.id = id;
        this.mailSystem = mailSystem;
        this.sender = sender;
        this.receiver = receiver;
        this.sendDateTime = sendDateTime;
        this.subject = subject;
        this.messageText = messageText;
        this.attachments = attachments;
        this.read = read;
    }
    
    public MailMessage updateReceiver(GameProfile receiver) {
        return new MailMessage(id, mailSystem, sender, receiver, sendDateTime, subject, messageText, attachments, read);
    }

    public int getId() {
        return id;
    }

    @Override
    public IMailSystem getMailSystem() {
        return mailSystem;
    }

    @Override
    public GameProfile getSender() {
        return sender;
    }

    @Override
    public GameProfile getReceiver() {
        return receiver;
    }

    @Override
    public Date getSendDateTime() {
        return sendDateTime;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getMessageText() {
        return messageText;
    }

    @Override
    public NonNullList<ItemStack> getAttachments() {
        return attachments;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "MailMessage [id=" + id + ", mailSystem=" + mailSystem + ", sender=" + sender + ", receiver=" + receiver + ", sendDateTime=" + sendDateTime + ", subject=" + subject + ", messageText=" + messageText + ", attachments=" + attachments + "]";
    }
}
