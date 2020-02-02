package moe.plushie.rpg_framework.mail.common;

import java.util.List;

import moe.plushie.rpg_framework.api.core.IGuiIcon;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.utils.PlayerUtils;
import moe.plushie.rpg_framework.core.database.TableMail;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MailSystem implements IMailSystem, Comparable<IMailSystem> {

    private final IIdentifier identifier;
    private final String name;
    private int characterLimit;
    private ICost messageCost;
    private ICost attachmentCost;
    private int inboxSize;
    private int maxAttachments;
    private boolean sendingEnabled;
    private boolean allowSendingToSelf;
    private boolean mailboxFlagRender;
    private int mailboxFlagRenderDistance;
    private boolean chatNotificationAtLogin;
    private boolean chatNotificationOnNewMessage;
    private boolean toastNotificationAtLogin;
    private boolean toastNotificationOnNewMessage;
    private String costAlgorithm;
    private IGuiIcon[] guiIcons;

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
    public ICost getMessageCost() {
        return messageCost;
    }

    public MailSystem setMessageCost(ICost messageCost) {
        this.messageCost = messageCost;
        return this;
    }

    @Override
    public ICost getAttachmentCost() {
        return attachmentCost;
    }

    public MailSystem setAttachmentCost(ICost attachmentCost) {
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

    public boolean onClientSendMailMessage(EntityPlayerMP player, MailMessage mailMessage) {
        boolean susscess = TableMail.addMessage(mailMessage);
        List<EntityPlayerMP> playerEntityList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        for (int i = 0; i < playerEntityList.size(); i++) {
            EntityPlayerMP entityPlayerMP = playerEntityList.get(i);
            if (PlayerUtils.gameProfilesMatch(entityPlayerMP.getGameProfile(), mailMessage.getReceiver())) {
                RPGFramework.getProxy().getMailSystemManager().getNotificationManager().syncToClient(entityPlayerMP, false, true);
            }
        }
        return susscess;
    }

    public void onClientDeleteMessage(EntityPlayerMP entityPlayer, int messageId) {
        TableMail.deleteMessage(messageId);
    }

    public void onClientSelectMessage(EntityPlayerMP entityPlayer, int messageId) {
        TableMail.markMessageasRead(messageId);
        RPGFramework.getProxy().getMailSystemManager().getNotificationManager().syncToClient(entityPlayer, false, false);
    }
}
