package moe.plushie.rpg_framework.core.common.network.client;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.inventory.ContainerMailBox;
import moe.plushie.rpg_framework.mail.common.serialize.MailMessageSerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiMailBox implements IMessage, IMessageHandler<MessageClientGuiMailBox, IMessage> {

    private MailMessageType messageType;
    private MailMessage[] mailMessages;
    private IMailSystem mailSystem;
    private int messageId;

    public MessageClientGuiMailBox() {
    }

    public MessageClientGuiMailBox(MailMessageType messageType) {
        this.messageType = messageType;
    }

    public MessageClientGuiMailBox setMailMessages(MailMessage[] mailMessages) {
        this.mailMessages = mailMessages;
        return this;
    }

    public MessageClientGuiMailBox setMailSystem(IMailSystem mailSystem) {
        this.mailSystem = mailSystem;
        return this;
    }

    public MessageClientGuiMailBox setMessageId(int messageId) {
        this.messageId = messageId;
        return this;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(messageType.ordinal());
        switch (messageType) {
        case MAIL_CHANGE:
            break;
        case MAIL_MESSAGE_SEND:
            buf.writeInt(mailMessages.length);
            for (int i = 0; i < mailMessages.length; i++) {
                JsonElement jsonElement = MailMessageSerializer.serializeJson(mailMessages[i], true);
                ByteBufUtils.writeUTF8String(buf, jsonElement.toString());
            }
            break;
        case MAIL_MESSAGE_REQUEST:
            break;
        case MAIL_MESSAGE_SELECT:
            writeMailSystem(buf, mailSystem);
            buf.writeInt(messageId);
            break;
        case MAIL_MESSAGE_DELETE:
            writeMailSystem(buf, mailSystem);
            buf.writeInt(messageId);
            break;
        case MAIL_MESSAGE_WITHDRAW_ITEMS:
            writeMailSystem(buf, mailSystem);
            buf.writeInt(messageId);
            break;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        messageType = MailMessageType.values()[buf.readInt()];
        switch (messageType) {
        case MAIL_CHANGE:
            break;
        case MAIL_MESSAGE_SEND:
            int count = buf.readInt();
            mailMessages = new MailMessage[count];
            for (int i = 0; i < count; i++) {
                String mailJson = ByteBufUtils.readUTF8String(buf);
                mailMessages[i] = MailMessageSerializer.deserializeJson(SerializeHelper.stringToJson(mailJson));
            }
            break;
        case MAIL_MESSAGE_REQUEST:
            break;
        case MAIL_MESSAGE_SELECT:
            mailSystem = readMailSystem(buf);
            messageId = buf.readInt();
            break;
        case MAIL_MESSAGE_DELETE:
            mailSystem = readMailSystem(buf);
            messageId = buf.readInt();
            break;
        case MAIL_MESSAGE_WITHDRAW_ITEMS:
            mailSystem = readMailSystem(buf);
            messageId = buf.readInt();
            break;
        }
    }
    
    private void writeMailSystem(ByteBuf buf, IMailSystem mailSystem) {
        JsonElement json = IdentifierSerialize.serializeJson(mailSystem.getIdentifier());
        ByteBufUtils.writeUTF8String(buf, json.toString());
    }
    
    private IMailSystem readMailSystem(ByteBuf buf) {
        JsonElement json = SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf));
        IIdentifier identifier = IdentifierSerialize.deserializeJson(json);
        return RPGFramework.getProxy().getMailSystemManager().getMailSystem(identifier);
    }

    @Override
    public IMessage onMessage(MessageClientGuiMailBox message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (message.messageType.getNeedsCreative()) {
            if (!player.capabilities.isCreativeMode) {
                RPGFramework.getLogger().warn(String.format("Player %s tried to use the shop action %s without creative mode.", player.getName(), message.messageType.toString()));
                return null;
            }
        }
        
        if (!(player.openContainer != null && player.openContainer instanceof ContainerMailBox)) {
            return null;
        }
        
        ContainerMailBox containerMailBox = (ContainerMailBox) player.openContainer;

        switch (message.messageType) {
        case MAIL_CHANGE:

            break;
        case MAIL_MESSAGE_SEND:
            containerMailBox.onClientSendMailMessages(player, message.mailMessages);
            containerMailBox.markDirty();
            break;
        case MAIL_MESSAGE_REQUEST:

            break;
        case MAIL_MESSAGE_SELECT:
            containerMailBox.onClientSelectMessage(player, message.messageId);
            break;
        case MAIL_MESSAGE_DELETE:
            containerMailBox.onClientDeleteMessage(player, message.messageId);
            containerMailBox.markDirty();
            break;
        case MAIL_MESSAGE_WITHDRAW_ITEMS:
            containerMailBox.onClientWithdrawItems(player, message.messageId);
            containerMailBox.markDirty();
            break;
        }
        
        return null;
    }

    public static enum MailMessageType {
        /** Change linked mail system. */
        MAIL_CHANGE(true),

        /** Send mail. */
        MAIL_MESSAGE_SEND(false),

        /** Request message from server. */
        MAIL_MESSAGE_REQUEST(false),

        /** Set the active message. */
        MAIL_MESSAGE_SELECT(false),

        /** Delete a mail message. */
        MAIL_MESSAGE_DELETE(false),
        
        MAIL_MESSAGE_WITHDRAW_ITEMS(false);

        private final boolean needsCreative;

        private MailMessageType(boolean needsCreative) {
            this.needsCreative = needsCreative;
        }

        public boolean getNeedsCreative() {
            return needsCreative;
        }
    }
}
