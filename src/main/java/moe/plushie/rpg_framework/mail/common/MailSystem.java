package moe.plushie.rpg_framework.mail.common;

import moe.plushie.rpg_framework.api.core.IGuiIcon;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.mail.IMailMessage;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import net.minecraft.entity.player.EntityPlayerMP;

public class MailSystem implements IMailSystem, Comparable<IMailSystem> {

    private final IIdentifier identifier;
    private final String name;
    private int characterLimit = 500;
    private ICurrency currency;
    private int messageCost = 0;
    private int attachmentCost = 0;
    private int inboxSize = 50;
    private int maxAttachments = 9;
    private boolean sendingEnabled = true;
    private boolean allowSendingToSelf = false;
    private boolean mailboxFlagRender = true;
    private int mailboxFlagRenderDistance = 32;
    private boolean chatNotificationAtLogin = true;
    private boolean chatNotificationOnNewMessage = true;
    private boolean toastNotificationAtLogin = true;
    private boolean toastNotificationOnNewMessage = true;
    private String costAlgorithm = "var result = function() {return ($messageCost + $attachmentCost * $attachmentCount)};";
    private IGuiIcon[] guiIcons = new IGuiIcon[] {};

    public MailSystem(IIdentifier identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    @Override
    public IIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCharacterLimit() {
        return characterLimit;
    }

    public MailSystem setCharacterLimit(int characterLimit) {
        this.characterLimit = characterLimit;
        return this;
    }

    @Override
    public ICurrency getCurrency() {
        return currency;
    }

    public MailSystem setCurrency(ICurrency currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public int getMessageCost() {
        return messageCost;
    }

    public MailSystem setMessageCost(int messageCost) {
        this.messageCost = messageCost;
        return this;
    }

    @Override
    public int getAttachmentCost() {
        return attachmentCost;
    }

    public MailSystem setAttachmentCost(int attachmentCost) {
        this.attachmentCost = attachmentCost;
        return this;
    }

    @Override
    public int getInboxSize() {
        return inboxSize;
    }

    public MailSystem setInboxSize(int inboxSize) {
        this.inboxSize = inboxSize;
        return this;
    }

    @Override
    public int getMaxAttachments() {
        return maxAttachments;
    }

    public MailSystem setMaxAttachments(int maxAttachments) {
        this.maxAttachments = maxAttachments;
        return this;
    }

    public MailSystem setSendingEnabled(boolean sendingEnabled) {
        this.sendingEnabled = sendingEnabled;
        return this;
    }

    @Override
    public boolean isSendingEnabled() {
        return this.sendingEnabled;
    }

    public MailSystem setAllowSendToSelf(boolean allowSendingToSelf) {
        this.allowSendingToSelf = allowSendingToSelf;
        return this;
    }

    @Override
    public boolean isAllowSendingToSelf() {
        return allowSendingToSelf;
    }

    public MailSystem setAllowSendingToSelf(boolean allowSendingToSelf) {
        this.allowSendingToSelf = allowSendingToSelf;
        return this;
    }

    @Override
    public boolean isMailboxFlagRender() {
        return mailboxFlagRender;
    }

    public MailSystem setMailboxFlagRender(boolean mailboxFlagRender) {
        this.mailboxFlagRender = mailboxFlagRender;
        return this;
    }

    @Override
    public int getMailboxFlagRenderDistance() {
        return mailboxFlagRenderDistance;
    }

    public MailSystem setMailboxFlagRenderDistance(int mailboxFlagRenderDistance) {
        this.mailboxFlagRenderDistance = mailboxFlagRenderDistance;
        return this;
    }

    @Override
    public boolean isChatNotificationAtLogin() {
        return chatNotificationAtLogin;
    }

    public MailSystem setChatNotificationAtLogin(boolean chatNotificationAtLogin) {
        this.chatNotificationAtLogin = chatNotificationAtLogin;
        return this;
    }

    @Override
    public boolean isChatNotificationOnNewMessage() {
        return chatNotificationOnNewMessage;
    }

    public MailSystem setChatNotificationOnNewMessage(boolean chatNotificationOnNewMessage) {
        this.chatNotificationOnNewMessage = chatNotificationOnNewMessage;
        return this;
    }

    @Override
    public boolean isToastNotificationAtLogin() {
        return toastNotificationAtLogin;
    }

    public MailSystem setToastNotificationAtLogin(boolean toastNotificationAtLogin) {
        this.toastNotificationAtLogin = toastNotificationAtLogin;
        return this;
    }

    @Override
    public boolean isToastNotificationOnNewMessage() {
        return toastNotificationOnNewMessage;
    }

    public MailSystem setToastNotificationOnNewMessage(boolean toastNotificationOnNewMessage) {
        this.toastNotificationOnNewMessage = toastNotificationOnNewMessage;
        return this;
    }

    public MailSystem setCostAlgorithm(String costAlgorithm) {
        this.costAlgorithm = costAlgorithm;
        return this;
    }

    @Override
    public String getCostAlgorithm() {
        return this.costAlgorithm;
    }

    public MailSystem setGuiIcons(IGuiIcon[] guiIcons) {
        this.guiIcons = guiIcons;
        return this;
    }

    @Override
    public IGuiIcon[] getGuiIcons() {
        return this.guiIcons;
    }

    @Override
    public int compareTo(IMailSystem o) {
        return name.compareTo(o.getName());
    }

    public boolean sendMailMessage(IMailMessage mailMessage) {
        return TableMail.addMessage(mailMessage);
    }

    public void deleteMessage(int messageId) {
        TableMail.deleteMessage(messageId);
    }

    public void markMessageasRead(int messageId) {
        TableMail.markMessageasRead(messageId);
    }

    public void notifyClient(EntityPlayerMP entityPlayer) {
        RPGFramework.getProxy().getMailSystemManager().getNotificationManager().syncToClient(entityPlayer, this, false, false);
    }
}
