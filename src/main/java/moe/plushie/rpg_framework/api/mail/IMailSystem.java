package moe.plushie.rpg_framework.api.mail;

import moe.plushie.rpg_framework.api.core.IGuiIcon;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;

public interface IMailSystem {

    public IIdentifier getIdentifier();

    public String getName();

    public int getCharacterLimit();

    public ICost getMessageCost();

    public ICost getAttachmentCost();

    public int getInboxSize();

    public int getMaxAttachments();

    public boolean isSendingEnabled();

    public boolean isAllowSendingToSelf();

    public boolean isMailboxFlagRender();

    public int getMailboxFlagRenderDistance();

    public boolean isChatNotificationAtLogin();

    public boolean isChatNotificationOnNewMessage();

    public boolean isToastNotificationAtLogin();

    public boolean isToastNotificationOnNewMessage();

    public String getCostAlgorithm();

    public IGuiIcon[] getGuiIcons();
}
