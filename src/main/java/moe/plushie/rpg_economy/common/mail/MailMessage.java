package moe.plushie.rpg_economy.common.mail;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public class MailMessage {
    
    private final String usernameSender;
    private final String usernameReceiver;
    private final String subject;
    private final String messageText;
    private final ArrayList<ItemStack> attachments;
    
    public MailMessage(String usernameSender, String usernameReceiver, String subject, String messageText, ArrayList<ItemStack> attachments) {
        this.usernameSender = usernameSender;
        this.usernameReceiver = usernameReceiver;
        this.subject = subject;
        this.messageText = messageText;
        this.attachments = attachments;
    }
    
    public String getUsernameSender() {
        return usernameSender;
    }
    
    public String getUsernameReceiver() {
        return usernameReceiver;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public ArrayList<ItemStack> getAttachments() {
        return attachments;
    }
    
    public String getMessageText() {
        return messageText;
    }
    
    @Override
    public String toString() {
        return "MailMessage [usernameSender=" + usernameSender + ", usernameReceiver=" + usernameReceiver + ", subject=" + subject + ", messageText=" + messageText + ", attachments="
                + attachments + "]";
    }
}
