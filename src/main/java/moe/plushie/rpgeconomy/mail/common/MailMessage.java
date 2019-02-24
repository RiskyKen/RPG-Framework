package moe.plushie.rpgeconomy.mail.common;

import java.util.Calendar;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpgeconomy.api.mail.IMailMessage;
import moe.plushie.rpgeconomy.api.mail.IMailSystem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class MailMessage implements IMailMessage {

    private final MailSystem mailSystem;
    private final GameProfile sender;
    private final GameProfile receiver;
    private final Calendar sendDateTime;
    private final String subject;
    private final String messageText;
    private final NonNullList<ItemStack> attachments;

    public MailMessage(MailSystem mailSystem, GameProfile sender, GameProfile receiver, Calendar sendDateTime, String subject, String messageText, NonNullList<ItemStack> attachments) {
        this.mailSystem = mailSystem;
        this.sender = sender;
        this.receiver = receiver;
        this.sendDateTime = sendDateTime;
        this.subject = subject;
        this.messageText = messageText;
        this.attachments = attachments;
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
    public Calendar getSendDateTime() {
        return sendDateTime;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessageText() {
        return messageText;
    }

    public NonNullList<ItemStack> getAttachments() {
        return attachments;
    }

    @Override
    public String toString() {
        return "MailMessage [sender=" + sender + ", receiver=" + receiver + ", sendDateTime=" + sendDateTime + ", subject=" + subject + ", messageText=" + messageText + ", attachments=" + attachments + "]";
    }
}
