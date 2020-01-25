package moe.plushie.rpg_framework.mail.common;

import java.util.List;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
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
    private boolean allowSendingToSelf;

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

    public MailSystem setAllowSendToSelf(boolean allowSendingToSelf) {
        this.allowSendingToSelf = allowSendingToSelf;
        return this;
    }

    @Override
    public boolean getAllowSendToSelf() {
        return this.allowSendingToSelf;
    }

    @Override
    public int compareTo(IMailSystem o) {
        return name.compareTo(o.getName());
    }

    public MailSystem setMaxAttachments(int maxAttachments) {
        this.maxAttachments = maxAttachments;
        return this;
    }

    public boolean onClientSendMailMessage(EntityPlayerMP player, MailMessage mailMessage) {
        boolean susscess = TableMail.addMessage(mailMessage);
        List<EntityPlayerMP> playerEntityList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        for (int i = 0; i < playerEntityList.size(); i++) {
            EntityPlayerMP entityPlayerMP = playerEntityList.get(i);
            if (entityPlayerMP.getGameProfile().getId().equals(mailMessage.getReceiver().getId()) | entityPlayerMP.getGameProfile().getName().equals(mailMessage.getReceiver().getName())) {
                RPGFramework.getProxy().getMailSystemManager().getNotificationManager().syncToClient(entityPlayerMP, true);
            }
        }
        return susscess;
    }

    public void onClientDeleteMessage(EntityPlayerMP entityPlayer, int messageId) {
        TableMail.deleteMessage(messageId);
    }

    public void onClientSelectMessage(EntityPlayerMP entityPlayer, int messageId) {
        TableMail.markMessageasRead(messageId);
        RPGFramework.getProxy().getMailSystemManager().getNotificationManager().syncToClient(entityPlayer, false);
    }
}
