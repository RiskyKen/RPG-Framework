package moe.plushie.rpgeconomy.mail.common;

import moe.plushie.rpgeconomy.api.mail.IMailSystem;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.currency.common.Wallet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class MailSystem implements IMailSystem {

    private final String name;
    private int characterLimit;
    private Wallet messageCost;
    private Wallet attachmentCost;
    private int inboxSize;
    private int maxAttachments;

    public MailSystem(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public int getCharacterLimit() {
        return characterLimit;
    }

    public MailSystem setCharacterLimit(int characterLimit) {
        this.characterLimit = characterLimit;
        return this;
    }

    public Wallet getMessageCost() {
        return messageCost;
    }

    public MailSystem setMessageCost(Wallet messageCost) {
        this.messageCost = messageCost;
        return this;
    }

    public Wallet getAttachmentCost() {
        return attachmentCost;
    }

    public MailSystem setAttachmentCost(Wallet attachmentCost) {
        this.attachmentCost = attachmentCost;
        return this;
    }

    public int getInboxSize() {
        return inboxSize;
    }

    public MailSystem setInboxSize(int inboxSize) {
        this.inboxSize = inboxSize;
        return this;
    }

    public int getMaxAttachments() {
        return maxAttachments;
    }

    public MailSystem setMaxAttachments(int maxAttachments) {
        this.maxAttachments = maxAttachments;
        return this;
    }

    public void onClientSendMailMessage(EntityPlayerMP entityPlayer, MailMessage mailMessage) {
        for (int i = 0; i < mailMessage.getAttachments().size(); i++) {
            ItemStack itemStack = mailMessage.getAttachments().get(i);
            entityPlayer.entityDropItem(itemStack, entityPlayer.eyeHeight);
        }
        RpgEconomy.getLogger().info("got mail message from player " + mailMessage);
    }
}
