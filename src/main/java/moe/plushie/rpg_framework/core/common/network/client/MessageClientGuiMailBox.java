package moe.plushie.rpg_framework.core.common.network.client;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.inventory.ContainerMailBox;
import moe.plushie.rpg_framework.mail.common.serialize.MailMessageSerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiMailBox implements IMessage, IMessageHandler<MessageClientGuiMailBox, IMessage> {

    private MailMessageType messageType;
    private MailMessage mailMessage;
    private IMailSystem mailSystem;
    private int messageId;

    public MessageClientGuiMailBox() {
    }

    public MessageClientGuiMailBox(MailMessageType messageType) {
        this.messageType = messageType;
    }

    public MessageClientGuiMailBox setMailMessage(MailMessage mailMessage) {
        this.mailMessage = mailMessage;
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
            JsonElement jsonElement = MailMessageSerializer.serializeJson(mailMessage, true);
            ByteBufUtils.writeUTF8String(buf, jsonElement.toString());
            break;
        case MAIL_MESSAGE_REQUEST:
            break;
        case MAIL_MESSAGE_SELECT:
            break;
        case MAIL_MESSAGE_DELETE:
            JsonElement json = IdentifierSerialize.serializeJson(mailSystem.getIdentifier());
            ByteBufUtils.writeUTF8String(buf, json.toString());
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
            String mailJson = ByteBufUtils.readUTF8String(buf);
            mailMessage = MailMessageSerializer.deserializeJson(SerializeHelper.stringToJson(mailJson));
            break;
        case MAIL_MESSAGE_REQUEST:
            break;
        case MAIL_MESSAGE_SELECT:
            break;
        case MAIL_MESSAGE_DELETE:
            JsonElement json = SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf));
            IIdentifier identifier = IdentifierSerialize.deserializeJson(json);
            mailSystem = RpgEconomy.getProxy().getMailSystemManager().getMailSystem(identifier);
            messageId = buf.readInt();
            break;
        }
    }

    @Override
    public IMessage onMessage(MessageClientGuiMailBox message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (message.messageType.getNeedsCreative()) {
            if (!player.capabilities.isCreativeMode) {
                RpgEconomy.getLogger().warn(String.format("Player %s tried to use the shop action %s without creative mode.", player.getName(), message.messageType.toString()));
                return null;
            }
        }

        switch (message.messageType) {
        case MAIL_CHANGE:

            break;
        case MAIL_MESSAGE_SEND:
            RpgEconomy.getProxy().getMailSystemManager().onClientSendMailMessage(player, message.mailMessage);
            break;
        case MAIL_MESSAGE_REQUEST:

            break;
        case MAIL_MESSAGE_SELECT:

            break;
        case MAIL_MESSAGE_DELETE:
            ((MailSystem) message.mailSystem).onClientDeleteMessage(player, message.messageId);
            break;
        }
        if (player.openContainer != null && player.openContainer instanceof ContainerMailBox) {
            ((ContainerMailBox) player.openContainer).markDirty();
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

        /**  */
        MAIL_MESSAGE_DELETE(false);

        private final boolean needsCreative;

        private MailMessageType(boolean needsCreative) {
            this.needsCreative = needsCreative;
        }

        public boolean getNeedsCreative() {
            return needsCreative;
        }
    }
}
