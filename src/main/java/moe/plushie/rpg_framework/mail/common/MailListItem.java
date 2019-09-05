package moe.plushie.rpg_framework.mail.common;

public class MailListItem {

    private final int id;
    private final String subject;
    private boolean hasItems;
    private boolean read;
    
    public MailListItem(int id, String subject, boolean hasItems, boolean read) {
        this.id = id;
        this.subject = subject;
        this.hasItems = hasItems;
        this.read = read;
    }

    public boolean hasItems() {
        return hasItems;
    }

    public void setHasItems(boolean hasItems) {
        this.hasItems = hasItems;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }
}
